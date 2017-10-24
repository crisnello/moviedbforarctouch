package com.crisnello.moviedb.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.crisnello.moviedb.MostrarNotificationActivity;
import com.crisnello.moviedb.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by crisnello
 */

public class MoviedbFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MoviedbFBMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "message : "+remoteMessage.getNotification().getBody());

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MostrarNotificationActivity.class);
        intent.putExtra("message",remoteMessage.getNotification().getBody());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

//        PendingIntent p = PendingIntent.getActivity(this, ZERO, intent , ZERO);
//        PendingIntent p = PendingIntent.getBroadcast(getApplicationContext(),ZERO,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker(getString(R.string.msg_moviedb));
        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setSmallIcon(R.drawable.ic_laucher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_laucher));
        builder.setContentIntent(contentIntent);

//        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
//        String [] descs = new String[]{"Descrição 1", "Descrição 2", "Descrição 3", "Descrição 4"};
//        for(int i = 0; i < descs.length; i++){
//            style.addLine(descs[i]);
//        }
//        builder.setStyle(style);

        Notification n = builder.build();
        n.vibrate = new long[]{150, 300, 150, 600};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        nm.notify(R.drawable.ic_laucher, n);

        try{
            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(this, som);
            toque.play();
        }
        catch(Exception e){}
    }
}
