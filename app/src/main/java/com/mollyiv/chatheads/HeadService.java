package com.mollyiv.chatheads;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

/**
 * Foreground service. Creates a head view.
 * The pending intent allows to go back to the settings activity.
 */
public class HeadService extends Service {

    private final static int FOREGROUND_ID = 999;

    private HeadLayer mHeadLayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logServiceStarted();
        //final ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //manager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
        //    @Override
        //    public void onPrimaryClipChanged() {
        //        Log.d("AppLog", "changed to:" + manager.getText());
        //        // or this for the textview:
        //        // textView.setText(manager.getText());
        //    }
        //});
        initHeadLayer();
        PendingIntent pendingIntent = createPendingIntent();
        Notification notification = createNotification(pendingIntent);
        startForeground(FOREGROUND_ID, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroyHeadLayer();
        stopForeground(true);
        logServiceEnded();
    }

    private void initHeadLayer() {
        mHeadLayer = new HeadLayer(this);
    }

    private void destroyHeadLayer() {
        mHeadLayer.destroy();
        mHeadLayer = null;
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    private Notification createNotification(PendingIntent intent) {
        String channelId = "notification_channel_id";
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            CharSequence name = "notification_channel_name";
            String description = "notification_channel_desc";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            channel.enableLights(false);
            channel.setSound(null, null);
            channel.enableVibration(false);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle(getText(R.string.notificationTitle))
                .setContentText(getText(R.string.notificationText))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(intent)
                .build();
    }

    private void logServiceStarted() {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }

    private void logServiceEnded() {
        Toast.makeText(this, "Service ended", Toast.LENGTH_SHORT).show();
    }
}
