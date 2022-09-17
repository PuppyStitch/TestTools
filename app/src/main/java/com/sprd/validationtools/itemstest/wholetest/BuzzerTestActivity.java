package com.sprd.validationtools.itemstest.wholetest;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

import java.io.IOException;

public class BuzzerTestActivity extends BaseActivity {

    private static final String TAG = "BuzzerTestActivity";

    private Button mStartButton, mStopButton;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout barcodeLayout = new LinearLayout(this);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        barcodeLayout.setLayoutParams(params);
        barcodeLayout.setOrientation(LinearLayout.VERTICAL);
        barcodeLayout.setGravity(Gravity.CENTER);
        mStartButton = new Button(this);
        mStartButton.setTextSize(35);
        mStopButton = new Button(this);
        mStopButton.setTextSize(35);
        barcodeLayout.addView(mStartButton);
        barcodeLayout.addView(mStopButton);
        setContentView(barcodeLayout);
        setTitle(R.string.buzzer_test);
        mStartButton.setText(getResources().getText(R.string.start_play));
        mStartButton.setOnClickListener(view -> start());
        mStopButton.setText(getResources().getText(R.string.stop_play));
        mStopButton.setOnClickListener(view -> stop());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = new MediaPlayer();
        AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.mixtone);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        try {
            mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                    file.getLength());
            mMediaPlayer.prepare();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void stop() {
        mMediaPlayer.pause();
    }

}
