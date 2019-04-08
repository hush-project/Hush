package com.hushproject.hush;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

public class EditActivity extends AppCompatActivity {

    private TextView locName;
    private TextView locAddress;

    private String name = "";
    private String address = "";
    private int ringVolume = 0;
    private int mediVolume = 0;
    private int notiVolume = 0;
    private int systVolume = 0;

    Gson gson = new Gson();

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        //set location text.
        locAddress.setText(editLocation.getLocationAddress());

       //get ringer values to prevent settings defaulting to 0 if none are changed.
        ringVolume = editLocation.getLocRingVol();
        mediVolume = editLocation.getLocMediVol();
        notiVolume = editLocation.getLocNotiVol();
        systVolume = editLocation.getLocSystVol();

        //seekbars
        final SeekBar ringVol = findViewById(R.id.ringVol);
        ringVol.setProgress(editLocation.getLocRingVol());
        ringVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int barVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                barVal = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ringVolume = barVal;
            }
        });

        final SeekBar mediVol = findViewById(R.id.mediVol);
        mediVol.setProgress(editLocation.getLocMediVol());
        mediVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int barVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                barVal = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediVolume = barVal;
            }
        });

        final SeekBar notiVol = findViewById(R.id.notiVol);
        notiVol.setProgress(editLocation.getLocNotiVol());
        notiVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int barVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                barVal = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                notiVolume = barVal;
            }
        });

        final SeekBar systVol = findViewById(R.id.systVol);
        systVol.setProgress(editLocation.getLocSystVol());
        systVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int barVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                barVal = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                systVolume = barVal;
            }
        });

    }

    public void setAddress(View view) {
        /*
        This method is for setting the address variable (so it can be saved to file)
        after a location is chosen in the map activity.
         */

        Intent openMap = new Intent(this, MapActivity.class);
        startActivity(openMap);
    }

    public void saveLoc(View view) {
        //Test string for address.
        address = "Test";

        //Store current values as a UserLocations object.
        UserLocations saveLocation = new UserLocations(name, address, ringVolume,
                mediVolume, notiVolume, systVolume);

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
