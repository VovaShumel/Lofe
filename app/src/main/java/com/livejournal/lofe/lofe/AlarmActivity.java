package com.livejournal.lofe.lofe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.livejournal.lofe.lofe.model.LofeRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static com.livejournal.lofe.lofe.MyUtil.log;

public class AlarmActivity extends FragmentActivity implements View.OnTouchListener {

    float x, y;
    TextView tvX, tvY;
    ImageView ivClock;
    CheckBox cbAlarmEnabled, cbNotificationEnabled;
    long PressTime = 0;
    long recordId;
    AlarmManager alarmManager;
    Long ms = null;
    LofeRecord record;
    Button bGoToAlarmTriggeredActivity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        Intent intent = getIntent();
        recordId = intent.getLongExtra("recordId", 0L);
        record = intent.getParcelableExtra("RECORD");

        tvX = findViewById(R.id.textView);
        tvY = findViewById(R.id.textView2);

        ivClock = findViewById(R.id.iView);
        ivClock.setOnTouchListener(this);

        cbAlarmEnabled = findViewById(R.id.cbSetAlarm_aSetAlarm);
        cbNotificationEnabled = findViewById(R.id.cbSetNotification_aSetAlarm);

        bGoToAlarmTriggeredActivity = findViewById(R.id.bSetAlarm_testGoToAlarmTriggeredActivity);
        bGoToAlarmTriggeredActivity.setOnClickListener(view -> {
            Intent newIntent = new Intent(AlarmActivity.this, AlarmTriggeredActivity.class);
            newIntent.putExtra("recordId", recordId);
            newIntent.putExtra("disallowBack", true);
            startActivity(newIntent);
        });

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        final CalendarView calendarView = findViewById(R.id.calendarView);

//        btSetAlarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // REFACT удалить это после отладки основного
//                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
//                pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 10000, pendingIntent);
//            }
//        });

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            try {
                month += 1;
                ms = smf.parse("" + dayOfMonth + "/" + month + "/" + year + " 00:00:00").getTime();
                ms += TimeZone.getDefault().getRawOffset();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus)
            tvY.setText(ivClock.getLeft() + ", " + ivClock.getTop());
    }

    @Override
    public void onResume() {
        super.onResume();

        ImageView ivClock = findViewById(R.id.iView);
        tvY.setText(ivClock.getLeft() + ", " + ivClock.getTop());
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        if (v.getId() == R.id.iView) {
            final int coo[] = new int[2];

            Rect rect = new Rect();
            v.getGlobalVisibleRect(rect);

            x = event.getX();
            y = event.getY();

            v.getLocationOnScreen(coo);

            switch (event.getAction()) {

                case ACTION_DOWN:
                    PressTime = System.currentTimeMillis();
                    break;

                case ACTION_UP:
                    tvX.setText(x + ", " + y);

                    double phi = Math.atan2(x - 360, -(y - 360));
                    if (phi < 0)
                        phi = 2 * Math.PI + phi;
                    int hm = (int)Math.round(phi / ((2 * Math.PI) / 144));
                    int h = hm / 12;
                    int m = (hm % 12) * 5;
                    if ((System.currentTimeMillis() - PressTime) > 700)
                        h += 12;

                    tvY.setText(h + ":" + m);
                    tvX.setText((System.currentTimeMillis() / 1000) + " ");

                    if (ms == null) {
                        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();

                        int curH = calendar.get(Calendar.HOUR_OF_DAY);
                        int curM = calendar.get(Calendar.MINUTE);

                        calendar.set(Calendar.HOUR_OF_DAY, h);
                        calendar.set(Calendar.MINUTE, m);
                        calendar.set(Calendar.SECOND, 0);

                        if ((curH > h) || ((curH == h) && (curM > m)))
                            calendar.add(Calendar.DAY_OF_YEAR, 1);

                        ms = calendar.getTimeInMillis();
                    }
                    else {
                        try {
                            SimpleDateFormat smf = new SimpleDateFormat("HH:mm");
                            ms += smf.parse("" + h + ":" + m + "").getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    Intent intent = new Intent();
                    //intent.putExtra("ms", ms);//REFACT удалить после отладки правильного
                    record.setTime(ms);
                    record.setIsAlarmEnabled(cbAlarmEnabled.isChecked());
                    intent.putExtra("RECORD", record);
                    setResult(RESULT_OK, intent);

//                    if (cbAlarmEnabled.isChecked()) {// REFACT удалить после отладки правильного
//                        intent = new Intent(getApplicationContext(), AlarmReceiver.class);
//                        intent.putExtra("recordId", recordId);
//                        log("AlarmActivity recordId = " + recordId);
//
//                        pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                        alarmManager.set(AlarmManager.RTC_WAKEUP, ms, pendingIntent);
//                    }

                    finish();
                    break;
            }
            return true;
        }
        return false;
    }
}
