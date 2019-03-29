package com.hushproject.hush;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

public class AddActivity extends AppCompatActivity {

    //create EditText and TextView objects.
    private EditText locName;
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
        setContentView(R.layout.activity_add);

        //Create a SharedPreferences object and get our shared preferences.
        SharedPreferences locPrefs = getSharedPreferences("LocPrefs", MainActivity.MODE_PRIVATE);
        //set up an editor for SharedPreferences so we can save our data.
        editor = locPrefs.edit();

        //Create & attach textfields.
        locName = findViewById(R.id.locationName);
        locAddress = findViewById(R.id.address);

        //seekbars
        final SeekBar ringVol = findViewById(R.id.ringVol);
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
        //open our map activity.
        Intent openMap = new Intent(this, MapActivity.class);
        startActivity(openMap);

    }

    public void saveLoc(View view) {
        //set name to whatever the text in our EditText field is.
        name = locName.getText().toString();

        address = "Test";

        //create a new UserLocations object to store the information gathered by the activity.
        UserLocations newLocation = new UserLocations(name, address, ringVolume,
                mediVolume, notiVolume, systVolume);

        //turn newLocation into a gson string
        String loc = gson.toJson(newLocation);

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
