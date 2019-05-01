package com.hushproject.hush;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
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
    private static final int SEND_LOCATION_REQUEST = 1;
    GeocoderService geocoderService = new GeocoderService();

    private AudioManager audioManager;

    private Gson gson = new Gson();

    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

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
        address = openEdit.getString("cardAddress");
        locName.setText(name);

        //Convert gson string to object.
        String locToEdit = locPrefs.getString(name, "");
        UserLocations editLocation = gson.fromJson(locToEdit, UserLocations.class);

        //initialize location variables
        lat = editLocation.getLocationLat();
        lng = editLocation.getLocationLng();
        rad = editLocation.getLocationRad();

        //initialize volume variables to prevent accidentally setting them to 0.
        ringVolume = editLocation.getLocRingVol();
        mediVolume = editLocation.getLocMediVol();

        //seekbars
        final SeekBar ringVol = findViewById(R.id.ringVol);
        ringVol.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
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
        mediVol.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
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

                address = geocoderService.getAddressFromCoordinates(lat,lng,this);
                locAddress.setText(address);
            }
        }
    }

    public void saveLoc(View view)
    {
        //Store current values as a UserLocations object.
        UserLocations saveLocation = new UserLocations(name, address, lat, lng, rad, ringVolume, mediVolume);

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

    @Override
    public void onBackPressed() {
        Intent backToMain = new Intent(this, MainActivity.class);
        backToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(backToMain);
    }
}