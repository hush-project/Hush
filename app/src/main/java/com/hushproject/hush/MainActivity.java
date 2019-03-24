package com.hushproject.hush;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //create buttons for cards
    private Button editButton;
    private Button deleteButton;

    //Create RecyclerView to display our locations.
    private RecyclerView locationView;
    private RecyclerView.Adapter locationAdapter;

    //Create a SharedPreferences object.
    private SharedPreferences locPrefs;
    private SharedPreferences.Editor editor;

    //Create an ArrayList to store UserLocations.
    private ArrayList<UserLocations> locations;

    //create Gson object.
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create SharedPreferences file
        locPrefs = getSharedPreferences("LocPrefs", MODE_PRIVATE);
        editor = locPrefs.edit();

        //ArrayList for storing UserLocation objects.
        ArrayList<UserLocations> locations = new ArrayList<>();

        //ArrayList for storing location keys.
        ArrayList<String> locationKeys = new ArrayList<>();

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

        //bind buttons to appropriate elements.
        editButton = findViewById(R.id.editBtn);
        deleteButton = findViewById(R.id.delBtn);
    }

    public void add(View view) {
        //open the Add activity.
        Intent openAdd = new Intent(this, AddActivity.class);
        startActivity(openAdd);
    }

    public void edit(View view) {

        Log.d("Edit", "was pressed");
    }

    public void delete(View view) {

        Log.d("delete", "was pressed");
    }
}
