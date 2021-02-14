package com.livejournal.lofe.lofe;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.livejournal.lofe.lofe.model.LofeRecord;

import java.text.SimpleDateFormat;

import static com.livejournal.lofe.lofe.DBHelper.*;
import static com.livejournal.lofe.lofe.MyUtil.*;

public class AlarmTriggeredActivity extends FragmentActivity implements View.OnDragListener {

    TextView tvRecordText;
    long recordId;

    Button bDone, bSetNewTime, bSetTimePlus5Min, bSetTimePlusHour, bSetTimeForTomorrow;
    ImageView ivTrash;

    boolean containsDragable = false;

    AudioManager audioManager;

    MediaPlayer mpMusic;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_triggered);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        Intent intent = getIntent();
        recordId = intent.getLongExtra("recordId", 0L);

        tvRecordText = findViewById(R.id.TV_RecordText);
        tvRecordText.setText(getRecordText(recordId));
        tvRecordText.setOnClickListener(view -> {
            Intent newIntent = new Intent(this, AddEditRecordActivity.class);
            //newIntent.putExtra("recordId", recordId);
            newIntent.putExtra("id", recordId);
            //newIntent.putExtra("disallowBack", true);
            startActivity(newIntent);
        });
//        tvRecordText.setOnTouchListener((view, motionEvent) -> {
//            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                @SuppressLint("ClickableViewAccessibility") ClipData clipData= ClipData.newPlainText("","");
//                View.DragShadowBuilder dsb=new View.DragShadowBuilder(view);
//                view.startDrag(clipData, dsb, view,0);
//                view.setVisibility(View.INVISIBLE);
//                return true;
//            } else {
//                return false;
//            }
//        });

        bDone = findViewById(R.id.bDone_aAlarmTriggered);
        bDone.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                @SuppressLint("ClickableViewAccessibility") ClipData clipData= ClipData.newPlainText("","");
                View.DragShadowBuilder dsb=new View.DragShadowBuilder(view);
                view.startDrag(clipData, dsb, view,0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }); // TODO перенести потом обработчик на текст

        bSetNewTime = findViewById(R.id.bForNewTime_aAlarmTriggered);
        bSetNewTime.setOnClickListener(view -> {
            Intent newIntent = new Intent(AlarmTriggeredActivity.this, AlarmActivity.class);
            newIntent.putExtra("recordId", recordId);
            newIntent.putExtra("SET_ALARM_IN_THIS_ACTIVITY", true);
            startActivity(newIntent);
        });

        bSetTimePlus5Min = findViewById(R.id.bPlus5m_aAlarmTriggered);
        bSetTimePlus5Min.setOnClickListener(view -> SetRecordAlarmToANewTimeAndExit(getCurTimeMS() + (5 * 60 * 1000)));

        bSetTimePlusHour = findViewById(R.id.bPlusHour_aAlarmTriggered);
        bSetTimePlusHour.setOnClickListener(view -> SetRecordAlarmToANewTimeAndExit(getCurTimeMS() + (60 * 60 * 1000)));

        bSetTimeForTomorrow = findViewById(R.id.bForTomorrow_aAlarmTriggered);
        bSetTimeForTomorrow.setOnClickListener(view -> SetRecordAlarmToANewTimeAndExit(getCurTimeMS() + (24 * 60 * 60 * 1000)));

        ivTrash = findViewById(R.id.ivAlarmTriggered_trash);
        ivTrash.setOnDragListener(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // TODO зачем это?

        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        //KeyguardManager.
        lock.disableKeyguard();

        //-----
//        mpMusic = MediaPlayer.create(this, R.raw.rington);
//        mpMusic.start();
    }

//    @Override
//    public void onBackPressed() {
//        if (disallowBack) {
//            Intent i = new Intent(this, MainActivity.class);
//            i.putExtra("disallowBack", true);
//            startActivity(i);
//        } else
//            super.onBackPressed();
//    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        int dragAction = dragEvent.getAction();
        View dragView = (View) dragEvent.getLocalState();

        if (dragAction == DragEvent.ACTION_DRAG_EXITED)
            containsDragable = false;
        else if (dragAction == DragEvent.ACTION_DRAG_ENTERED)
            containsDragable = true;
        else if (dragAction == DragEvent.ACTION_DRAG_ENDED){
            if(dropEventNotHandled(dragEvent))
                dragView.setVisibility(View.VISIBLE);

        } else if (dragAction == DragEvent.ACTION_DROP && containsDragable) {
            //checkForValidMove((ChessBoardSquareLayoutView) view, dragView);
            delRec(recordId);
            Intent newIntent = new Intent(AlarmTriggeredActivity.this, MainActivity.class);
            newIntent.putExtra("disallowBack", true);
            startActivity(newIntent);
        }
        return true;
    }

    // TODO реализовать чтобы не было ошибки, если недотащили кнопку

    private boolean dropEventNotHandled(DragEvent dragEvent) {
        return !dragEvent.getResult();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void SetRecordAlarmToANewTimeAndExit(long ms) {
        edtRecordDate(recordId, ms);
        NotificationManagerCompat.from(this).cancel((int) recordId);
        LofeRecord record = getRecord(recordId);
        record.setAlarm(true);
        tat("Будильник установлен на " + new SimpleDateFormat("dd.MM.yy HH:mm").format(ms));
        Intent newIntent = new Intent(AlarmTriggeredActivity.this, MainActivity.class);
        newIntent.putExtra("disallowBack", true);
        startActivity(newIntent);
    }
}
