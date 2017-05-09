package com.livejournal.lofe.lofe;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by User on 26.03.2016.
 */
public class AlarmActivity extends FragmentActivity implements View.OnTouchListener {

    float x, y;
    TextView tvX, tvY;
    ImageView ivClock;
    int vx, vy;
    long PressTime = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        tvX = (TextView) findViewById(R.id.textView);
        tvY = (TextView) findViewById(R.id.textView2);

        ivClock = (ImageView) findViewById(R.id.iView);
        ivClock.setOnTouchListener(this);

        //tvY.setText(ivClock.getLeft() + ", " + ivClock.getTop());

//        ivClock.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            public void onGlobalLayout() {
//                //ivClock.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                ivClock.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                //getViewTreeObserver().removeGlobalOnLayoutListener(this);
//
//                int[] locations = new int[2];
//                ivClock.getLocationOnScreen(locations);
//                MyLog.d("Clock left = " + ivClock.getLeft());
//                MyLog.d("Clock top = " + ivClock.getTop());
//                MyLog.d("Clock x = " + locations[0]);
//                MyLog.d("Clock y = " + locations[0]);
//                //int x = locations[0];
//                //int y = locations[1];
//            }
//        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if(hasFocus) {
            //Toast.makeText(this, ivClock.getLeft() + ", " + ivClock.getTop(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, tvY.getLeft() + ", " + ivClock.getBottom(), Toast.LENGTH_LONG).show();
            tvY.setText(ivClock.getLeft() + ", " + ivClock.getTop());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //vx = ivClock.getLeft();
        //vy = ivClock.getTop();
        //tvY.setText(ivClock.getLeft() + ", " + ivClock.getTop());

        ImageView ivClock = (ImageView) findViewById(R.id.iView);
        //image.setOnTouchListener(this);
        tvY.setText(ivClock.getLeft() + ", " + ivClock.getTop());

        Rect rect = new Rect();
        //ivClock.getLocalVisibleRect(rect);
        //tvY.setText(rect.left + ", " + rect.top);
//        Log.d("WIDTH :",String.valueOf(rect.width()));
//        Log.d("HEIGHT :",String.valueOf(rect.height()));
//        Log.d("left :",String.valueOf(rect.left));
//        Log.d("right :",String.valueOf(rect.right));
//        Log.d("top :",String.valueOf(rect.top));
//        Log.d("bottom :", String.valueOf(rect.bottom));

    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {

        if (v.getId() == R.id.iView) {

            //final int[] locations = new int[2];
            final int coo[] = new int[2];

            Rect rectt = new Rect();
            v.getGlobalVisibleRect(rectt);

            //tvY.setText(rectt.left + ", " + rectt.top);

            //tvY.setText(ivClock.getLeft() + ", " + ivClock.getTop());

//            v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                public void onGlobalLayout() {
//                    v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//
//                    int[] locations = new int[2];
//                    v.getLocationOnScreen(locations);
//                    //tvY.setText(locations[0] + ", " + locations[1]);
//                    //int x = locations[0];
//                    //int y = locations[1];
//                }
//            });

            x = event.getX();
            y = event.getY();

            //event.

            //Toast.makeText(this, x + ", " + y, Toast.LENGTH_LONG).show();


            //v.getLocationInWindow(coo);
            v.getLocationOnScreen(coo);

            switch (event.getAction()) {

                //case MotionEvent.ACTION_CANCEL:
                case ACTION_DOWN:
                    //tvY.setText(locations[0] + ", " + locations[1]);
                    //tvY.setText(coo[0] + ", " + coo[1]);
                    //tvX.setText(x + ", " + y);
                    PressTime = System.currentTimeMillis();
                    break;

                case ACTION_UP:
                    tvX.setText(x + ", " + y);
                    //tvY.setText(coo[0] + ", " + coo[1]);

//                    double z = Math.atan((x - 360) / (y - 360)) / (Math.PI / 2 / 180);
//                    z = 360 - z;
//                    tvY.setText(Math.round(z / 60) + " h " + Math.round(z % 60) + " m");

                    double phi = Math.atan2(x - 360, -(y - 360));
                    if (phi < 0)
                        phi = 2 * Math.PI + phi;
                    int hm = (int)Math.round(phi / ((2 * Math.PI) / 144));
                    int h = hm / 12;
                    int m = (hm % 12) * 5;
                    //z = 360 - z;
                    //tvY.setText(phi + " ");
                    if ((System.currentTimeMillis() - PressTime) > 700)
                        h += 12;

                    tvY.setText(h + ":" + m);
                    tvX.setText((System.currentTimeMillis() / 1000) + " ");



                    break;
            }
            return  true;
        }
        return false;
    }
}
