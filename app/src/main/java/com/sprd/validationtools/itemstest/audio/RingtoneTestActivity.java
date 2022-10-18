package com.sprd.validationtools.itemstest.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

public class RingtoneTestActivity extends BaseActivity {

    private static final String TAG = "RingtoneTestActivity";

    private MediaPlayer mediaPlayer;
    private AudioManager mAudioManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(this, R.raw.mixtone);
        mAudioManager = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onResume() {

        super.onResume();
        try {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    int volumeMusic = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    Log.d(TAG, "volumeMusic = " + volumeMusic);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeMusic, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.reset();
    }
}
