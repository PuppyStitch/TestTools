package com.sprd.validationtools.itemstest.wholetest;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

public class BuzzerTestActivity extends BaseActivity {

    private static final String TAG = "BuzzerTestActivity";

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(this, R.raw.beep);
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
