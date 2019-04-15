package com.hushproject.hush;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

public class EditActivity extends AppCompatActivity
{
    private TextView locName;
    private TextView locAddress;

    private String name = "";
    private String address = "";
    private double lat;
    private double lng;
    private int rad;
    private int ringVolume = 0;
    private int mediVolume = 0;
    private int notiVolume = 0;
    private int systVolume = 0;
    private static final int SEND_LOCATION_REQUEST = 1;

    Gson gson = new Gson();

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //textfields.
        locName = findViewById(R.id.locationName);
        locAddress = findViewById(R.id.address);

        //Get SharedPreferences
        SharedPreferences locPrefs = getSharedPreferences("LocPrefs", MainActivity.MODE_PRIVATE);
        //preference editor.
        editor = locPrefs.edit();

        //Get the name of the profile and set our name variable equal to it.
        Bundle openEdit = getIntent().getExtras();

        name = openEdit.getString("cardKey");

        locName.setText(name);

        //Convert gson string to object.
        String locToEdit = locPrefs.getString(name, "");
        UserLocations editLocation = gson.fromJson(locToEdit, UserLocations.class);

        //initialize location variables
        lat = editLocation.getLocationLat();
        lng = editLocation.getLocationLng();
        rad = editLocation.getLocationRad();


        //set text for edit geofence button. Variables need renaming.
        address = "Tap here to edit your geofence.";
        locAddress.setText(address);

        //initialize volume variables to prevent accidentally setting them to 0.
        ringVolume = editLocation.getLocRingVol();
        mediVolume = editLocation.getLocMediVol();
        notiVolume = editLocation.getLocNotiVol();
        systVolume = editLocation.getLocSystVol();

        //seekbars
        final SeekBar ringVol = findViewById(R.id.ringVol);
        ringVol.setMax(7);
        ringVol.setProgress(editLocation.getLocRingVol());
        ringVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            int barVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                barVal = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ringVolume = barVal;
            }
        });

        final SeekBar mediVol = findViewById(R.id.mediVol);
        mediVol.setMax(15);
        mediVol.setProgress(editLocation.getLocMediVol());
        mediVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            int barVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

                barVal = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediVolume = barVal;
            }
        });

        final SeekBar notiVol = findViewById(R.id.notiVol);
        notiVol.setMax(7);
        notiVol.setProgress(editLocation.getLocNotiVol());
        notiVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            int barVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                barVal = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                notiVolume = barVal;
            }
        });

        final SeekBar systVol = findViewById(R.id.systVol);
        systVol.setMax(7);
        systVol.setProgress(editLocation.getLocSystVol());
        systVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            int barVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                barVal = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                systVolume = barVal;
            }
        });

    }

    public void setAddress(View view)
    {

        Intent openMapEdit = new Intent(this, MapEditActivity.class);
        openMapEdit.putExtra("lati", lat);
        openMapEdit.putExtra("long", lng);
        openMapEdit.putExtra("rad", rad);
        startActivityForResult(openMapEdit, SEND_LOCATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SEND_LOCATION_REQUEST) {
            if(resultCode == RESULT_OK) {
                lat = data.getDoubleExtra("latitude", 0.0);
                lng = data.getDoubleExtra("longitude", 0.0);
                rad = data.getIntExtra("radius", 0);
            }
        }
    }

    public void saveLoc(View view)
    {
        //Store current values as a UserLocations object.
        UserLocations saveLocation = new UserLocations(name, lat, lng, rad, ringVolume, mediVolume, notiVolume, systVolume);

        //Convert to a json string.
        String loc = gson.toJson(saveLocation);

        //Save json string to preferences.
        editor.putString(name, loc);
        editor.commit();

        Intent returnToMain = new Intent(this, MainActivity.class);
        //Kill activity to save memory.
        returnToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(returnToMain);
    }
}