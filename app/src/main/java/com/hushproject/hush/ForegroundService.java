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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.hushproject.hush.App.CHANNEL_ID;

public class ForegroundService extends Service {

    private LocationListener listener;
    private LocationManager locationManager;

    private SharedPreferences locPrefs;
    private ArrayList<String> locationKeys;
    private ArrayList<UserLocations> locations;

    private Gson gson = new Gson();

    private double curLat;
    private double curLng;

    DatabaseReference database;
    private GeoFire geoFire;

    private AudioManager audioManager;

    private Timer timer = new Timer();
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

        //Create SharedPreferences
        locPrefs = getSharedPreferences("LocPrefs", MODE_PRIVATE);

        locationKeys = new ArrayList<>();

        locations = new ArrayList<>();

        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

        database = FirebaseDatabase.getInstance().getReference("fences");

        geoFire = new GeoFire(database);

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

        locationManager =
                (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Updating", "Service is running."
                                + " " + curLat + " " + curLng);
                        checkGPS();
                        checkFence();
                    }
                });
            }
        };

        long delay = 0;
        long period = 120 * 1000;

        timer.scheduleAtFixedRate(task, delay, period); //runs every 2 minutes.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        NotificationCompat.Action closeAction
                = new NotificationCompat.Action
                .Builder(R.drawable.ic_close, "Close", pendingIntent).build();

        Notification notification
                = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Hush")
                .setContentText("Hush is listening for geofence transitions.")
                .setSmallIcon(R.drawable.ic_android)
                .addAction(closeAction)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        getSharedPrefs();
        updateFences();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

    public void updateFences() {
        for(int i = 0; i < locations.size(); i++) {
            UserLocations current = locations.get(i);

            Log.d("currentdata", "is: " + current.getLocationLat()
            + " " + current.getLocationLat());

            geoFire.setLocation(current.getLocationName(),
                    new GeoLocation(current.getLocationLat(),
                            current.getLocationLng()),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            Log.d("Database Entry: ", "created.");
                        }
                    });
        }
    }

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

    public void checkFence() {

        LatLng radius = new LatLng(curLat, curLng);

        GeoQuery locationQuery = geoFire
                .queryAtLocation(new GeoLocation(radius.latitude, radius.longitude),
                        0.5);
        locationQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("User entered location:", " " + key);
                String savedLoc = locPrefs.getString(key, "");
                //convert json string back to object.
                UserLocations savedLocation = gson.fromJson(savedLoc, UserLocations.class);

                int ringVol = savedLocation.getLocRingVol();
                int mediVol = savedLocation.getLocMediVol();
                int notiVol = savedLocation.getLocNotiVol();
                int systVol = savedLocation.getLocSystVol();

                if(ringVol == 0) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
                            AudioManager.ADJUST_MUTE, 0);
                }
                else {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
                            AudioManager.ADJUST_UNMUTE, 0);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING,
                            ringVol, 0);
                }

                if(mediVol == 0) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_MUTE, 0);
                }
                else {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_UNMUTE, 0);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            mediVol, 0);
                }

                if(notiVol == 0) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION,
                            AudioManager.ADJUST_MUTE, 0);
                }
                else {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION,
                            AudioManager.ADJUST_UNMUTE, 0);
                    audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
                            notiVol, 0);
                }

                if(systVol == 0) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM,
                            AudioManager.ADJUST_MUTE, 0);
                }
                else {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM,
                            AudioManager.ADJUST_UNMUTE, 0);
                    audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                            systVol, 0);
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

}
