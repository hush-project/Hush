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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

public class MapEditActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Double latitude;
    private Double longitude;
    private int radius;
    private Circle myCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_edit);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle openMapEdit = getIntent().getExtras();

        latitude = openMapEdit.getDouble("lati", 0.0);
        longitude = openMapEdit.getDouble("long", 0.0);
        radius = openMapEdit.getInt("rad", 0);

        Button send = findViewById(R.id.sendBtn);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Latitude is: ", "" + latitude);
                Log.d("Longitude is: ", "" + longitude);
                Log.d("Radius is: ", "" + radius);
                Intent e = getIntent();
                e.putExtra("latitude", latitude);
                e.putExtra("longitude", longitude);
                e.putExtra("radius", radius);
                setResult(RESULT_OK, e);
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
        LatLng circleLatLng = new LatLng(latitude, longitude);
        final SeekBar setRadius = findViewById(R.id.circleRadius);

        checkPermissions();

        myCircle = googleMap.addCircle(new CircleOptions()
                .clickable(true)
                .center(circleLatLng)
                .radius(radius)
                .strokeColor(Color.DKGRAY)
                .fillColor(Color.LTGRAY));

        zoomSavedLocation();


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
            }
        });

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
    public void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    public void zoomSavedLocation() {
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
                                location.setLatitude(latitude);
                                location.setLongitude(longitude);
                                Log.d("Location is", "" + location);
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(new LatLng(location.getLatitude(),
                                                location.getLongitude()), 17.0f));
                            }
                        }
                    }
                });
    }
}
