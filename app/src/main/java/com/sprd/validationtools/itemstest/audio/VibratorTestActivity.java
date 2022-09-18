package com.sprd.validationtools.itemstest.audio;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.sprd.validationtools.BaseActivity;

public class VibratorTestActivity extends BaseActivity {

    private static final String TAG = "VibratorTestActivity";

    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVibrator != null) {
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(10000, 200);
            mVibrator.vibrate(vibrationEffect);
            Log.i(TAG, "Vibrator");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVibrator != null) {
            mVibrator.cancel();
        }
    }
}
