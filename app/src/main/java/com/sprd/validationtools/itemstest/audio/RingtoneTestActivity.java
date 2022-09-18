package com.sprd.validationtools.itemstest.audio;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

public class RingtoneTestActivity extends BaseActivity {

    private static final String TAG = "RingtoneTestActivity";

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(this, R.raw.mixtone);
    }

    @Override
    protected void onResume() {

        super.onResume();
        try {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
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
