package com.livejournal.lofe.lofe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class AlarmActivity extends FragmentActivity implements View.OnTouchListener {

    float x, y;
    TextView tvX, tvY;
    ImageView ivClock;
    Button btSetAlarm;
    long PressTime = 0;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Long ms = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        tvX = (TextView) findViewById(R.id.textView);
        tvY = (TextView) findViewById(R.id.textView2);

        ivClock = (ImageView) findViewById(R.id.iView);
        ivClock.setOnTouchListener(this);

        btSetAlarm = (Button) findViewById(R.id.buttonSetAlarm);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        final CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        btSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //final Intent my_intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                // intent.putExtra("id", 0); // TODO положить здесь id записи, для которой будильник

                pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 10000, pendingIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 10000, pendingIntent);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {

                SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                try {
                    month += 1;
                    ms = smf.parse("" + dayOfMonth + "/" + month + "/" + year + " 00:00:00").getTime();
                    ms += TimeZone.getDefault().getRawOffset();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if(hasFocus) {
            //Toast.makeText(this, tvY.getLeft() + ", " + ivClock.getBottom(), Toast.LENGTH_LONG).show();
            tvY.setText(ivClock.getLeft() + ", " + ivClock.getTop());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ImageView ivClock = (ImageView) findViewById(R.id.iView);
        tvY.setText(ivClock.getLeft() + ", " + ivClock.getTop());
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {

        if (v.getId() == R.id.iView) {

            final int coo[] = new int[2];

            Rect rectt = new Rect();
            v.getGlobalVisibleRect(rectt);

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

                    Intent intent = new Intent().putExtra("ms", ms);
                    setResult(RESULT_OK, intent);
                    finish();

                    break;
            }
            return  true;
        }
        return false;
    }
}
