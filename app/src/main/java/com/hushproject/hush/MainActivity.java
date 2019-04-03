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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Recyclerview
    private RecyclerView locationView;
    private RecyclerView.Adapter locationAdapter;

    /*
    SharedPreference object for user locations. We don't need an editor because this activity
    doesn't edit location preferences, it only retrieves them.
     */
    private SharedPreferences locPrefs;

    //ArrayList to store UserLocations objects.
    private ArrayList<UserLocations> locations;

    //ArrayList to store location keys.
    private ArrayList<String> locationKeys;

    //ArrayList to store geofences.

    //Gson object.
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create SharedPreferences files
        locPrefs = getSharedPreferences("LocPrefs", MODE_PRIVATE);

        //ArrayList for storing UserLocations.
        locations = new ArrayList<>();

        //ArrayList for storing location keys.
        locationKeys = new ArrayList<>();

        //get all SharedPreferences keys and store them in a list.
        Map<String, ?> keys = locPrefs.getAll();
        for(Map.Entry<String, ?> entries : keys.entrySet()) {
            locationKeys.add(entries.getKey());
        }

        /*
        Loop through the location keys, convert them back from strings, put them in
        locations ArrayList.
         */
        for(int i = 0; i < locationKeys.size(); i++) {
            //find object using key.
            String savedLoc = locPrefs.getString(locationKeys.get(i), "");
            //convert gson string back to object.
            UserLocations savedLocation = gson.fromJson(savedLoc, UserLocations.class);
            //add recovered object to locations ArrayList.
            locations.add(savedLocation);
        }

        //set up LocationViewAdapter and feed in locations ArrayList.
        locationAdapter = new LocationViewAdapter(locations);
        //set locationView to locationViewer.
        locationView = findViewById(R.id.locationViewer);
        //set locationView's adapter to locationAdapter.
        locationView.setAdapter(locationAdapter);
        //set locationView's layout manager to a linear layout manager.
        locationView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void add(View view) {
        //open the Add activity.
        Intent openAdd = new Intent(this, AddActivity.class);
        startActivity(openAdd);
    }
}
