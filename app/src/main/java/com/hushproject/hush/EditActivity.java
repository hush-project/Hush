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
    //create EditText and TextView objects.
    private TextView locName;
    private TextView locAddress;

    //create private member variables for use in storing/writing to file.
    private String name = "";
    private String address = "";
    private int ringVolume = 0;
    private int mediVolume = 0;
    private int notiVolume = 0;
    private int systVolume = 0;

    //create Gson object.
    Gson gson = new Gson();

    //create SharedPreferences editor.
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //Create & attach textfields.
        locName = findViewById(R.id.locationName);
        locAddress = findViewById(R.id.address);

        //Create a SharedPreferences object and get our shared preferences.
        SharedPreferences locPrefs = getSharedPreferences("LocPrefs", MainActivity.MODE_PRIVATE);
        //set up an editor for SharedPreferences so we can save our data.
        editor = locPrefs.edit();

        //Get the name of the profile and set our name variable equal to it.
        Bundle openEdit = getIntent().getExtras();

        name = openEdit.getString("cardKey");

        locName.setText(name);

        //Create an object to convert our gson string back into.

        String locToEdit = locPrefs.getString(name, "");

        UserLocations editLocation = gson.fromJson(locToEdit, UserLocations.class);

        /*
        Here we initialize our member variables so our user's volume settings don't get set back to
        0 in the event they click save without altering the seekbar progress.
         */
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
    }

    public void saveLoc(View view) {
        //set name to whatever the text in our EditText field is.

        address = "Test";

        //create a new UserLocations object to store the information gathered by the activity.
        UserLocations saveLocation = new UserLocations(name, address, ringVolume,
                mediVolume, notiVolume, systVolume);

        //turn newLocation into a gson string
        String loc = gson.toJson(saveLocation);

        //commit gson string to SharedPreferences using location name as the key.
        editor.putString(name, loc);
        editor.commit();

        //return to MainActivity
        Intent returnToMain = new Intent(this, MainActivity.class);
        //kill activity to save memory.
        returnToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(returnToMain);
    }
}
