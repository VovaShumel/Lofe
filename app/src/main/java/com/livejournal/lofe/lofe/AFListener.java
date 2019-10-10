package com.livejournal.lofe.lofe;

import android.media.AudioManager;
import android.media.MediaPlayer;

import static com.livejournal.lofe.lofe.MyUtil.log;

public class AFListener implements AudioManager.OnAudioFocusChangeListener {
    String label = "";
    MediaPlayer mp;

    public AFListener(MediaPlayer mp, String label) {
        this.label = label;
        this.mp = mp;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        String event = "";
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                event = "AUDIOFOCUS_LOSS";
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                event = "AUDIOFOCUS_LOSS_TRANSIENT";
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                event = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                event = "AUDIOFOCUS_GAIN";
                break;
        }
        log(label + " onAudioFocusChange: " + event);
    }
}
