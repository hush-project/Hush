package com.hushproject.hush;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;


public class MapEditActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private SharedPreferences locPrefs;
    private SharedPreferences.Editor editor;
    private UserLocations current;
    private String name = "";
    private int latitude;
    private int longitude;
    private int radius;
    private static final String gk = "";
    private Circle myCircle;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_edit);
        /*
        // Initialize Places.
        Places.initialize(getApplicationContext(), gk);
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        */
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Get SharedPreferences
        SharedPreferences locPrefs = getSharedPreferences("LocPrefs", MainActivity.MODE_PRIVATE);
        //preference editor.
        editor = locPrefs.edit();

        //Get the name of the profile and set our name variable equal to it.
        Bundle openMapEdit = getIntent().getExtras();
        //name functions as key for retrieving & saving sharedpreferences.
        name = openMapEdit.getString("locKey");
        //check to see if we are getting the key we need to retrieve associated preferences.
        Log.d("Name is", ""  + name);
        String currentMap = locPrefs.getString(name,"");
        current = gson.fromJson(currentMap, UserLocations.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        FloatingActionButton searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        final SeekBar setRadius = findViewById(R.id.circleRadius);

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


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            if(current.getLocationLat() == 0.0 && current.getLocationLng() == 0.0) {
                                Log.d("Location is", "" + location);
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(new LatLng(location.getLatitude(),
                                                location.getLongitude()), 20.0f));
                            }
                            else {
                                location.setLatitude(current.getLocationLat());
                                location.setLongitude(current.getLocationLat());
                                Log.d("Location is", "" + location);
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(new LatLng(location.getLatitude(),
                                                location.getLongitude()), 20.0f));
                            }
                        }
                    }
                });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                myCircle = googleMap.addCircle(new CircleOptions()
                        .clickable(true)
                        .center(latLng)
                        .radius(5)
                        .strokeColor(Color.DKGRAY)
                        .fillColor(Color.LTGRAY));

                mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                    @Override
                    public void onCircleClick(Circle circle) {
                        setRadius.setVisibility(View.VISIBLE);
                        setRadius.setProgress((int) myCircle.getRadius());
                        setRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                myCircle.setRadius(progress);
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
}
