package com.hushproject.hush;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.hushproject.hush.App.CHANNEL_ID;

public class ForegroundService extends Service {

    private SharedPreferences locPrefs;
    private ArrayList<String> locationKeys;
    private ArrayList<UserLocations> locations;
    private Gson gson = new Gson();

    private Timer timer = new Timer();
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

        //Create SharedPreferences
        locPrefs = getSharedPreferences("LocPrefs", MODE_PRIVATE);

        locationKeys = new ArrayList<>();

        locations = new ArrayList<>();

        //Get all location keys.
        Map<String, ?> keys = locPrefs.getAll();
        for(Map.Entry<String, ?> entries : keys.entrySet()) {
            locationKeys.add(entries.getKey());
        }

        //Convert all json strings back into UserLocations.
        for(int i = 0; i < locationKeys.size(); i++) {
            //find object using key.
            String savedLoc = locPrefs.getString(locationKeys.get(i), "");
            //convert json string back to object.
            UserLocations savedLocation = gson.fromJson(savedLoc, UserLocations.class);
            //add recovered object to locations ArrayList.
            locations.add(savedLocation);
        }



        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Updating", "Service is running.");

                    }
                });
            }
            //sets delay (which I do not want) and draw period.
        }, 10000, 10000);
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
}
