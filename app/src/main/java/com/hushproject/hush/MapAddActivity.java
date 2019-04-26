package com.hushproject.hush;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapAddActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private double latitude;
    private double longitude;
    private double curLat;
    private double curLng;
    private int radius;
    private Circle myCircle;

    private LocationListener listener;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_add);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),
                    getResources().getString(R.string.google_maps_key));
        }

        locationManager =
                (LocationManager) getApplicationContext()
                        .getSystemService(Context.LOCATION_SERVICE);

        createGPSListener();

        Button send = findViewById(R.id.sendBtn);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Latitude is: ", "" + latitude);
                Log.d("Longitude is: ", "" + longitude);
                Log.d("Radius is: ", "" + radius);

                Intent i = getIntent();
                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);
                i.putExtra("radius", radius);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        final SeekBar setRadius = findViewById(R.id.circleRadius);

        setMyLocationLayer();

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("placesTag", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());

                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(place.getLatLng().latitude,
                                place.getLatLng().longitude), 17.0f));
            }

            @Override
            public void onError(Status status) {
                Log.i("errorTag", "An error occurred: " + status);
            }
        });

        getLastLocation();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                myCircle = googleMap.addCircle(new CircleOptions()
                        .clickable(true)
                        .center(latLng)
                        .radius(5)
                        .strokeColor(Color.DKGRAY)
                        .fillColor(0x40D6DBDF));

                latitude = latLng.latitude;
                longitude = latLng.longitude;
                radius = 5;
                mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                    @Override
                    public void onCircleClick(Circle circle) {
                        setRadius.setVisibility(View.VISIBLE);
                        setRadius.setProgress((int) myCircle.getRadius());
                        setRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                myCircle.setRadius(progress);
                                radius = progress;
                                if(progress == 0) {
                                    myCircle.remove();
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                setRadius.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });
    }

    public void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {

        }

        locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000,
                        1,
                        listener);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            if(latitude == 0.0 && longitude == 0.0) {
                                Log.d("Location is", "" + location);
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(new LatLng(location.getLatitude(),
                                                location.getLongitude()), 17.0f));
                            }
                            else {
                                location.setLatitude(curLat);
                                location.setLongitude(curLng);
                                Log.d("Location is", "" + location);
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(new LatLng(location.getLatitude(),
                                                location.getLongitude()), 17.0f));
                                mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(curLat, curLng))
                                    .title("You are here.")
                                );
                            }
                        }
                    }
                });
    }

    public void createGPSListener() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                curLat = location.getLatitude();
                curLng = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    public void setMyLocationLayer() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {

        }

        mMap.setMyLocationEnabled(true);
    }
}
