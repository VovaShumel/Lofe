package com.livejournal.lofe.lofe.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.livejournal.lofe.lofe.AlarmReceiver;
import com.livejournal.lofe.lofe.model.Alarm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static android.content.Intent.ACTION_BOOT_COMPLETED;

import static com.livejournal.lofe.lofe.AlarmReceiver.setReminderAlarms;
import static com.livejournal.lofe.lofe.DBHelper.*;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Executors.newSingleThreadExecutor().execute(() -> {
                //final List<Alarm> alarms = DatabaseHelper.getInstance(context).getAlarms();
                //setReminderAlarms(context, alarms);
                final List<Alarm> alarms = getAlarms();
                //setReminderAlarms(context, alarms);TODO
            });
        }
    }
}
