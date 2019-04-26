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
import static com.hushproject.hush.App.CHANNEL_ID2;

public class ForegroundService extends Service {

    private LocationListener listener;
    private LocationManager locationManager;

    private SharedPreferences locPrefs;
    private ArrayList<String> locationKeys;
    private ArrayList<UserLocations> locations;

    private Gson gson = new Gson();

    private String curName;
    private double curLat;
    private double curLng;

    private AudioManager audioManager;

    private NotificationManagerCompat notificationManager;

    //time intervals for the handler. startInterval is 10 seconds, regular interval is 1 minute.
    private int startInterval = 10000;
    private int interval = 60000;
    //handler for location checking.
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();

        //Create SharedPreferences
        locPrefs = getSharedPreferences("LocPrefs", MODE_PRIVATE);
        locationKeys = new ArrayList<>();
        locations = new ArrayList<>();

        //AudioManager declaration.
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        //NotificationManager declaration
        notificationManager = NotificationManagerCompat.from(this);

        createGPSListener();
        getSharedPrefs();

        locationManager =
                (LocationManager) getApplicationContext()
                        .getSystemService(Context.LOCATION_SERVICE);

        checkGPS();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                checkGPS();
                checkLocation();
                createNotification();

                handler.postDelayed(this, interval);
            }
        };

        handler.postDelayed(runnable, startInterval);
    }

    //this sets up the foreground notification for the app.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        //the foreground notification.
        Notification notification
                = new NotificationCompat.Builder(this, CHANNEL_ID1)
                .setContentTitle("Hush is running.")
                .setSmallIcon(R.drawable.ic_hush_button)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_REDELIVER_INTENT;
    }

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
    public void checkGPS() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        else {
            locationManager
                    .requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000,
                            1,
                            listener);
        }
    }

    /*
    * This method is the work horse of the foreground service.
    * It checks the user's location against the area within the radius of the circle.
    * If the user is within this radius, the app then changes their ringtone and music volumes.
    * It also updates the foreground notification to display what location they are currently in.
     */
    public void checkLocation() {
        UserLocations current;

        Location currentLocation = new Location(LocationManager.GPS_PROVIDER);

        float[] distance = new float[1];

        for(int i = 0; i < locations.size(); i++) {
            current = locations.get(i);
            currentLocation.setLatitude(current.getLocationLat());
            currentLocation.setLongitude(current.getLocationLng());

            Location.distanceBetween(curLat, curLng,
                    current.getLocationLat(), current.getLocationLng(), distance);

            if(distance[0] > current.getLocationRad()) {
                Log.d("Not in", "location: " + current.getLocationName());

            }
            else {

                Log.d("In", "location: " + current.getLocationName());

                curName = "You are in: " + current.getLocationName();

                int ringVol = current.getLocRingVol();
                int mediVol = current.getLocMediVol();

                if(ringVol == 0) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
                            AudioManager.ADJUST_MUTE, 0);
                }
                else {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
                            AudioManager.ADJUST_UNMUTE, 0);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, ringVol, 0);
                }

                if(mediVol == 0) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_MUTE, 0);
                }
                else {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_UNMUTE, 0);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mediVol, 0);
                }

            }
        }
    }

    public void createNotification() {
        Notification locationNotification
                = new NotificationCompat.Builder(this, CHANNEL_ID2)
                .setSmallIcon(R.drawable.ic_location_on)
                .setContentTitle("Hush")
                .setContentText(curName)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(2, locationNotification);
    }
}
