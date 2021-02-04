package com.livejournal.lofe.lofe;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.livejournal.lofe.lofe.model.Alarm;

import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.ALARM_SERVICE;
import static com.livejournal.lofe.lofe.DBHelper.getRecordText;
import static com.livejournal.lofe.lofe.MyUtil.log;

public class AlarmReceiver extends BroadcastReceiver {

    //private static final int NOTIFY_ID = 101;
    // TODO итак, сейчас проблема в том, что при нажатии на уведомление появляется экран
    // AlarmTriggeredActivity, но текста уведомления там нет
    // и снова проигрывается мелодия. Так не должно быть, не нужно тут мелодии, просто нужно просмотреть текст уведомления
    // и сделать с ним какие-то действия
    long recordId;

    @Override
    public void onReceive(Context context, Intent intent) {

        recordId = intent.getLongExtra("recordId", 0L);
        log("AlarmReceiver intent.getLongExtra(recordId = " + recordId);

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra("recordId", recordId);
        PendingIntent contentIntent = PendingIntent.getBroadcast(context,
                //0,
                (int)recordId,
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.icon_small)
                //.setContentTitle("Напоминание")
                .setContentText(getRecordText(recordId))
                //.setTicker("Обязательно!!!")
                .setLights(Color.YELLOW, 1000, 1000);
                   //(R.raw.rington) //TODO задавать звук здесь!

        //Notification notification = null;

        //notification.Set

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int)recordId, builder.build());

        Intent _intent = new Intent(context, AlarmTriggeredActivity.class);// Запускаем экран отображения текста уведомления
//        context.getApplicationContext().startActivity(_intent);
        _intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _intent.putExtra("recordId", recordId);
        context.getApplicationContext().startActivity(_intent);
    }

    public static void setReminderAlarm(Context context, Alarm alarm) {
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("recordId", alarm.getId());

        final PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                alarm.notificationId(),// TODO одно напоминание не затрёт другое, если будет назначено на то же время?
                intent,
                FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.getTime(), pIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getTime(), pIntent);

        //ScheduleAlarm.with(context).schedule(alarm, pIntent);
    }

    public static void setReminderAlarms(Context context, List<Alarm> alarms) {
        for(Alarm alarm : alarms)
            setReminderAlarm(context, alarm);
    }

    public static void cancelReminderAlarm(Context context, Alarm alarm) {

        final Intent intent = new Intent(context, AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                alarm.notificationId(),
                intent,
                FLAG_UPDATE_CURRENT
        );

        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pIntent);
    }
}
