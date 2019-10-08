package com.livejournal.lofe.lofe;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.livejournal.lofe.lofe.MyUtil.log;

public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFY_ID = 101;
    long recordId;

    @Override
    public void onReceive(Context context, Intent intent) {

        recordId = intent.getLongExtra("recordId", 0L);
        log("AlarmReceiver intent.getLongExtra(recordId = " + recordId);

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent contentIntent = PendingIntent.getBroadcast(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Напоминание")
                .setContentText("Купить клей")
                .setTicker("Обязательно!!!")
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFY_ID, builder.build());

        Intent _intent = new Intent(context, AlarmTriggeredActivity.class);// TODO разобраться, зачем это нужно
//        _intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.getApplicationContext().startActivity(_intent);
        _intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _intent.putExtra("recordId", recordId);
        context.getApplicationContext().startActivity(_intent);
    }
}
