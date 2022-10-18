package com.sprd.validationtools.itemstest.wholetest;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

public class BuzzerTestActivity extends BaseActivity {

    private static final String TAG = "BuzzerTestActivity";

    private MediaPlayer mediaPlayer;
    private AudioManager mAudioManager = null;

    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 8000;
    private boolean isOk = false;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(BuzzerTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(BuzzerTestActivity.this, R.string.text_fail,
                        Toast.LENGTH_SHORT).show();
                storeRusult(false);
            }
            mHandler.removeCallbacks(runnable);
//            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(this, R.raw.beep);
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
                    mp.setLooping(true);
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.reset();
    }
}
