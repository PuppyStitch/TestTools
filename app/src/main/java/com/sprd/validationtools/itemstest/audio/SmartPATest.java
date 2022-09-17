package com.sprd.validationtools.itemstest.audio;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.content.Context;
import android.os.Message;
import android.graphics.Color;
import android.media.AudioManager;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

public class SmartPATest extends BaseActivity {
    private static final String TAG = "SmartPATest";
    private static final int SMARTPA_CALIBRATION_SUCCESS = 1;
    private static final int SMARTPA_CALIBRATION_FAIL = 0;

    private TextView mRisistanceTV = null;
    private AudioManager mAudioManager = null;
    private float mResistance = 0.0f;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SMARTPA_CALIBRATION_SUCCESS:
                    Log.d(TAG, "SMARTPA_CALIBRATION_SUCCESS, mResistance=:" + mResistance);
                    mRisistanceTV.setText(mResistance + "ohm");
                    mRisistanceTV.setTextColor(Color.GREEN);
                    Toast.makeText(SmartPATest.this, "SmartPATest Calibration Success!", Toast.LENGTH_SHORT).show();

                    this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            storeRusult(true);
                            finish();
                        }
                    }, 2000L);
                    break;
                case SMARTPA_CALIBRATION_FAIL:
                    Log.d(TAG, "SMARTPA_CALIBRATION_FAIL, mResistance=:" + mResistance);
                    mRisistanceTV.setText(mResistance + "ohm");
                    mRisistanceTV.setTextColor(Color.RED);
                    Toast.makeText(SmartPATest.this, "SmartPATest Calibration Fialed!", Toast.LENGTH_SHORT).show();

                    this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            storeRusult(false);
                            finish();
                        }
                    }, 2000L);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_smartpa_test);

        mRisistanceTV = (TextView) findViewById(R.id.risistance_tv);
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSmartpaTest();
            }
        }, 200L);
        super.onResume();
    }

    protected void startSmartpaTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                mAudioManager.setParameters("SmartAmpCalibration=1");
                String result =  mAudioManager.getParameters("SmartAmpCalibrationValues");
                Log.d(TAG, "result=:" + result);
                if (result == null || result.contains("null")) {
                    message.what = SMARTPA_CALIBRATION_FAIL;
                    mResistance = 0.0f;
                } else {
                    message.what = SMARTPA_CALIBRATION_SUCCESS;
                    try {
                        String valString = result.substring(result.lastIndexOf("=") + 1);
                        float val = (float) Integer.valueOf(valString) / 1024;
                        mResistance = (float) Math.round(val * 100) / 100;
                    } catch (Exception e) {
                        Log.d(TAG, "result can not transfer to Integer...");
                        mResistance = 0.0f;
                        e.printStackTrace();
                    }
                }
                mHandler.sendMessageDelayed(message, 1000L);
            }
        }).start();
    }
}
