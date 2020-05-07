package com.livejournal.lofe.lofe;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

import static com.livejournal.lofe.lofe.DBHelper.*;
import static com.livejournal.lofe.lofe.MyUtil.getCurTimeMS;
import static com.livejournal.lofe.lofe.MyUtil.log;

public class AlarmTriggeredActivity extends FragmentActivity {

    TextView tvRecordText;
    long recordId;

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
            Intent newIntent = new Intent(AlarmTriggeredActivity.this, AddEditRecordActivity.class);
            newIntent.putExtra("recordId", recordId);
            newIntent.putExtra("disallowBack", true);
            startActivity(newIntent);
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // TODO зачем это?

        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        //KeyguardManager.
        lock.disableKeyguard();

        //-----
        mpMusic = MediaPlayer.create(this, R.raw.rington);
        mpMusic.start();
    }
}
