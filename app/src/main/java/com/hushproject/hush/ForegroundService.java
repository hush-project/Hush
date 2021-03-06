package com.hushproject.hush;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Map;

import static com.hushproject.hush.App.CHANNEL_ID1;

public class ForegroundService extends Service {

    private LocationListener listener;
    private LocationManager locationManager;

    private SharedPreferences locPrefs;
    private ArrayList<String> locationKeys;
    private ArrayList<UserLocations> locations;

    private Gson gson = new Gson();

    private final String startText = "Hush is listening for a location.";
    private double curLat;
    private double curLng;

    private AudioManager audioManager;

    private NotificationManagerCompat notificationManager;

    //handler for location checking.
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();

        //SharedPreferences
        locPrefs = getSharedPreferences("LocPrefs", MODE_PRIVATE);
        locationKeys = new ArrayList<>();
        locations = new ArrayList<>();

        //AudioManager
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        //NotificationManager
        notificationManager = NotificationManagerCompat.from(this);

        //create GPS Listener.
        createGPSListener();

        //get SharedPreferences
        getSharedPrefs();

        //set up the location manager.
        locationManager =
                (LocationManager) getApplicationContext()
                        .getSystemService(Context.LOCATION_SERVICE);

        requestLocationUpdate();

        //Runnable for scheduled tasks.
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                requestLocationUpdate();
                checkLocation();

                //runs again after x milliseconds
                handler.postDelayed(this, 60000);
            }
        };

        //initial command to run the handler after x milliseconds.
        handler.postDelayed(runnable, 10000);
    }

    //this method runs when MainActivity makes a call to start the service.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();

        return START_REDELIVER_INTENT;
    }

    //standard onDestroy method. Cancels all notifications when service is destroyed.
    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancelAll();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //starts the foreground notification. This is private as we do not want anything but the
    //foreground service being able to start the foreground notification.
    private void startForeground()  {
        startForeground(1, foregroundNotification(startText));
    }

    //builds the foreground notification.
    public Notification foregroundNotification(String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID1)
                .setContentTitle("Hush is running.")
                .setContentText(text)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_hush_button)
                .setContentIntent(pendingIntent)
                .build();
    }

    //Updates the text on the foreground notification to display which location the user is in.
    public void updateNotification(String text) {
        Notification notification = foregroundNotification(text);

        notificationManager.notify(1, notification);
    }

    //method for fetching sharedprefs.
    public void getSharedPrefs() {
        locationKeys.clear();
        locations.clear();
        //Get all location keys.
        Map<String, ?> keys = locPrefs.getAll();
        for (Map.Entry<String, ?> entries : keys.entrySet()) {
            locationKeys.add(entries.getKey());
        }

        //Convert all json strings back into UserLocations.
        for (int i = 0; i < locationKeys.size(); i++) {
            //find object using key.
            String savedLoc = locPrefs.getString(locationKeys.get(i), "");
            //convert json string back to object.
            UserLocations savedLocation = gson.fromJson(savedLoc, UserLocations.class);
            //add recovered object to locations ArrayList.
            locations.add(savedLocation);
        }
    }

    //method for creating GPS listener.
    public void createGPSListener() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                curLat = location.getLatitude();
                curLng = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    //method for getting location updates.
    public void requestLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission", " not granted.");
        }
        else {
            locationManager
                    .requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000,
                            1,
                            listener);
        }
    }

    public void checkLocation() {
        UserLocations current;

        String locObj = "";
        String curName;

        Location currentLocation = new Location(LocationManager.GPS_PROVIDER);

        float[] distance = new float[1];

        for(int i = 0; i < locations.size(); i++) {
            current = locations.get(i);
            currentLocation.setLatitude(current.getLocationLat());
            currentLocation.setLongitude(current.getLocationLng());

            Location.distanceBetween(curLat, curLng,
                    current.getLocationLat(), current.getLocationLng(), distance);

            if(distance[0] < current.getLocationRad()) {

                locObj = locPrefs.getString(locationKeys.get(i), "");

            }
        }

        UserLocations activeLocation = gson.fromJson(locObj, UserLocations.class);

        if(!locObj.equalsIgnoreCase("")) {

            curName = "Current Location: " + activeLocation.getLocationName();

            int ringVol = activeLocation.getLocRingVol();
            int mediVol = activeLocation.getLocMediVol();

            updateNotification(curName);

            changeVols(ringVol, mediVol);
        }
        else  {

            updateNotification(startText);
        }

    }

    //method for changing phone volumes.
    public void changeVols(int ring, int medi) {

        if(ring == 0) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
                    AudioManager.ADJUST_MUTE, 0);
        }
        else {
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
                    AudioManager.ADJUST_UNMUTE, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, ring, 0);
        }

        if(medi == 0) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_MUTE, 0);
        }
        else {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_UNMUTE, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, medi, 0);
        }
    }
}
