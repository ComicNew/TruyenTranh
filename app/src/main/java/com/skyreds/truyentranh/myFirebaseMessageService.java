package com.skyreds.truyentranh;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.skyreds.truyentranh.activity.MainActivity;

public class myFirebaseMessageService extends FirebaseMessagingService {
    private String CHANNEL_ID = "my_channel_01";
    NotificationManager notificationManager;
    private static final String TAG = "MyFirebaseMsgService";
    NotificationCompat.Builder notificationBuilder;
    int notifyID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null) return;
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
        }

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data payload: " + remoteMessage.getData().toString());
        }


    }

    private void sendNotification(String messageBody,String title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.book)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 , notificationBuilder.build());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name =title;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
// Create a notification and set the notification channel.
            try {
                notificationManager.createNotificationChannel(mChannel);
            } catch (Exception ex){
                ex.printStackTrace();
            }

            notificationManager.notify(1 , notificationBuilder.build());

// Issue the notification.
        }

    }
}
