package com.hushproject.hush;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_ID1 = "foregroundServiceChannel";
    public static final String CHANNEL_ID2 = "locationNotificationChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel foregroundServiceChannel = new NotificationChannel(
                    CHANNEL_ID1,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationChannel locationNotificationChannel = new NotificationChannel(
                    CHANNEL_ID2,
                    "Location Notification Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(foregroundServiceChannel);
            manager.createNotificationChannel(locationNotificationChannel);
            foregroundServiceChannel.setShowBadge(false);
            locationNotificationChannel.setShowBadge(false);
        }
    }
}
