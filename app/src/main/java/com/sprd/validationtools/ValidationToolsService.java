package com.sprd.validationtools;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.IBinder;
import android.util.Log;

public class ValidationToolsService extends Service {
    private static final String TAG = "ValidationToolsNotification";
    private final static int NOTIFICATION_ID = android.os.Process.myPid();
    private static final String NOTIFICATION_CHANNEL_ID = TAG;

    public static final int FLAG_START_FOREGROUND = 1;
    public static final int FLAG_STOP_FOREGROUND = 2;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand startId=" + startId);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_LOW);
        channel.setSound(null, null);
        notificationManager.createNotificationChannel(channel);
        switch (intent.getFlags()) {
        case FLAG_START_FOREGROUND:
            Log.d(TAG, "Starting foreground");
            startForeground(NOTIFICATION_ID,
                    new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_dialog_info)
                            .build());
            break;
        case FLAG_STOP_FOREGROUND:
            Log.d(TAG, "Stopping foreground");
            stopForeground(true);
            break;
        default:
            Log.wtf(TAG, "Invalid flag on intent " + intent);
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}
