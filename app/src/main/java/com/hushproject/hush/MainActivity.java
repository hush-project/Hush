package com.hushproject.hush;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private GeofencingClient geofencingClient;

    private RecyclerView locationView;
    private RecyclerView.Adapter locationAdapter;

    private SharedPreferences locPrefs;

    private ArrayList<UserLocations> locations;

    private ArrayList<String> locationKeys;

    //ArrayList to store geofences.

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geofencingClient = LocationServices.getGeofencingClient(this);

        //Create SharedPreferences
        locPrefs = getSharedPreferences("LocPrefs", MODE_PRIVATE);

        locations = new ArrayList<>();

        locationKeys = new ArrayList<>();

        //Get all location keys.
        Map<String, ?> keys = locPrefs.getAll();
        for(Map.Entry<String, ?> entries : keys.entrySet()) {
            locationKeys.add(entries.getKey());
        }

        //Convert all json strings back into UserLocations.
        for(int i = 0; i < locationKeys.size(); i++) {
            //find object using key.
            String savedLoc = locPrefs.getString(locationKeys.get(i), "");
            //convert json string back to object.
            UserLocations savedLocation = gson.fromJson(savedLoc, UserLocations.class);
            //add recovered object to locations ArrayList.
            locations.add(savedLocation);
        }

        //LocationViewAdapter
        locationAdapter = new LocationViewAdapter(locations);
        locationView = findViewById(R.id.locationViewer);
        locationView.setAdapter(locationAdapter);
        locationView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void add(View view) {
        Intent openAdd = new Intent(this, AddActivity.class);
        startActivity(openAdd);
    }
}
