package com.livejournal.lofe.lofe;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.KEYGUARD_SERVICE;
import static com.livejournal.lofe.lofe.MyLog.d;

public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFY_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent contentIntent = PendingIntent.getBroadcast(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = context.getResources();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Напоминание")
                .setContentText("Купить клей")
                .setTicker("Обязательно!!!")
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFY_ID, builder.build());

        Intent _intent = new Intent(context, AlarmTriggeredActivity.class);
        _intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(_intent);
    }
}
