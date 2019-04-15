package com.hushproject.hush;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.gson.Gson;

public class AddActivity extends AppCompatActivity
{

    private EditText locName;
    private TextView locAddress;

    private String name = "";
    private double lat = 0;
    private double lng = 0;
    private int rad = 0;
    private int ringVolume = 0;
    private int mediVolume = 0;
    private int notiVolume = 0;
    private int systVolume = 0;
    private static final int SEND_LOCATION_REQUEST = 1;

    private AudioManager aManager;
    private Context context;

    private Gson gson = new Gson();

    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //AudioManager and Context
        aManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        context = getApplicationContext();

        //Get SharedPreferences.
        SharedPreferences locPrefs = getSharedPreferences("LocPrefs", MainActivity.MODE_PRIVATE);
        //SharedPreferences editor
        editor = locPrefs.edit();

        locName = findViewById(R.id.locationName);
        locAddress = findViewById(R.id.address);

        //seekbars
        final SeekBar ringVol = findViewById(R.id.ringVol);
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
        /*
        This method is for setting the address variable (so it can be saved to file)
        after a location is chosen in the map activity.
         */
        Intent openMapAdd = new Intent(this, MapAddActivity.class);
        startActivityForResult(openMapAdd, SEND_LOCATION_REQUEST);
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
        name = locName.getText().toString();

        //Store current values as a UserLocations object
        UserLocations newLocation = new UserLocations(name, lat, lng, rad, ringVolume,
                mediVolume, notiVolume, systVolume);

        //Convert to json string.
        String loc = gson.toJson(newLocation);

        //Save json string to preferences.
        editor.putString(name, loc);
        editor.commit();

        Intent returnToMain = new Intent(this, MainActivity.class);
        //kill activity to save memory.
        returnToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(returnToMain);
    }
}
