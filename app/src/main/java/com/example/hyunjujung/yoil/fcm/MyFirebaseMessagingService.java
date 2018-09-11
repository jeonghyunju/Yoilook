package com.example.hyunjujung.yoil.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.hyunjujung.yoil.MainActivity;
import com.example.hyunjujung.yoil.MyInfo;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.YoilMain;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by hyunjujung on 2017. 10. 17..
 */

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    Intent intent;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage.getData().get("message"));
    }

    private void sendNotification(String messageBody) {
        SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동 로그인일때
            if(messageBody.contains("팔로우")) {
                intent = new Intent(this, MyInfo.class);
            }else {
                intent = new Intent(this, YoilMain.class);
            }
        }else {
            intent = new Intent(this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notifications)
                .setContentTitle("알림")
                .setAutoCancel(true)
                .setSound(defaultUri)
                .setContentText(messageBody)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
