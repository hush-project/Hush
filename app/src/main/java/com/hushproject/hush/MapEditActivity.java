package com.hushproject.hush;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class MapEditActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private String name;
    private Double latitude;
    private Double longitude;
    private int radius;
    private Circle myCircle;
    private Marker addressMarker;

    private SharedPreferences locPrefs;
    private ArrayList<String> locationKeys;
    private ArrayList<UserLocations> locations;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_edit);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Create SharedPreferences
        locPrefs = getSharedPreferences("LocPrefs", MODE_PRIVATE);
        locationKeys = new ArrayList<>();
        locations = new ArrayList<>();

        getSharedPrefs();

        if(!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),
                    getResources().getString(R.string.google_maps_key));
        }

        Bundle openMapEdit = getIntent().getExtras();

        name = openMapEdit.getString("name", "");
        latitude = openMapEdit.getDouble("lati", 0.0);
        longitude = openMapEdit.getDouble("long", 0.0);
        radius = openMapEdit.getInt("rad", 0);

        Button send = findViewById(R.id.sendBtn);

        send.setOnClickListener(v -> {
            UserLocations current;
            boolean overLap = false;
            float[] distance = new float[1];

            for(int i = 0; i < locations.size(); i++) {
                current = locations.get(i);

                Location.distanceBetween(latitude, longitude,
                        current.getLocationLat(), current.getLocationLng(), distance);

                int curRadius = current.getLocationRad();
                int sumRadius = curRadius + radius;

                if(distance[0] < sumRadius
                        && !current.getLocationName().equalsIgnoreCase(name)) {
                    overLap = true;
                }

            }

            if(overLap) {
                Toast.makeText(getApplicationContext(),
                        "Error: Location overlaps an existing location.",
                        Toast.LENGTH_SHORT).show();
            }
            else {
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

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
                if (addressMarker != null)
                {
                    addressMarker.remove();
                }
                addressMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude))
                        .title("Address Location."));
            }

            @Override
            public void onError(Status status) {
                Log.i("errorTag", "An error occurred: " + status);
            }
        });

        LatLng circleLatLng = new LatLng(latitude, longitude);
        final SeekBar setRadius = findViewById(R.id.circleRadius);

        //load saved circle.
        loadSavedCircle(googleMap, circleLatLng);

        zoomSavedLocation();

        setRadius.setVisibility(View.VISIBLE);
        setRadius.setProgress((int) myCircle.getRadius());
        setRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                myCircle.setRadius(progress);
                radius = progress;
                if(progress == 0) {
                    myCircle.remove();
                    setRadius.setVisibility(View.GONE);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mMap.setOnMapClickListener(latLng -> createNewCircle(googleMap, latLng, setRadius));
    }

    public void loadSavedCircle(GoogleMap map, LatLng latLng) {
        //load saved circle.
        myCircle = map.addCircle(new CircleOptions()
                .clickable(true)
                .center(latLng)
                .radius(radius)
                .strokeColor(Color.DKGRAY)
                .fillColor(0x40D6DBDF));
        Log.d("Latitude", "is: " + latLng.latitude);
        Log.d("Longitude", "is: " + latLng.longitude);

    }

    public void zoomSavedLocation() {
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
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        if(latitude == 0.0 && longitude == 0.0) {
                            Log.d("Location is", "" + location);
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(new LatLng(location.getLatitude(),
                                            location.getLongitude()), 17.0f));
                        }
                        else {
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            Log.d("Location is", "" + location);
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(new LatLng(location.getLatitude(),
                                            location.getLongitude()), 17.0f));
                        }
                    }
                });
    }

    public void createNewCircle(GoogleMap map, LatLng latLng, SeekBar seekBar) {
        map.clear();
        myCircle = map.addCircle(new CircleOptions()
                .clickable(true)
                .center(latLng)
                .radius(5)
                .strokeColor(Color.DKGRAY)
                .fillColor(0x40D6DBDF));
        Log.d("Latitude", "is: " + latitude);
        Log.d("Longitude", "is: " + longitude);

        latitude = (Math.round(latLng.latitude*1000000.0) / 1000000.0);
        longitude = (Math.round(latLng.longitude*1000000.0) / 1000000.0);

        seekBar.setVisibility(View.VISIBLE);
        seekBar.setProgress((int) myCircle.getRadius());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                myCircle.setRadius(progress);
                radius = progress;
                if(progress == 0) {
                    myCircle.remove();
                    seekBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        }

        mMap.setMyLocationEnabled(true);
    }

    //Fetch SharedPreferences.
    public void getSharedPrefs() {
        locationKeys.clear();
        locations.clear();
        //Get all location keys.
        Map<String, ?> keys = locPrefs.getAll();
        for (Map.Entry<String, ?> entries : keys.entrySet()) {
            locationKeys.add(entries.getKey());
        }

        //Convert all json strings back into UserLocations.
        for (int i = 0; i < locationKeys.size(); i++) {
            //find object using key.
            String savedLoc = locPrefs.getString(locationKeys.get(i), "");
            //convert json string back to object.
            UserLocations savedLocation = gson.fromJson(savedLoc, UserLocations.class);
            //add recovered object to locations ArrayList.
            locations.add(savedLocation);
        }
    }
}