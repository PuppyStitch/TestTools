package com.sprd.validationtools.itemstest.wholetest;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

public class FlashTestActivity extends BaseActivity {

    private static final String TAG = "FlashTestActivity";
    private CameraManager camManager;
    String cameraId = null;

    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 8000;
    private boolean isOk = true;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(FlashTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(FlashTestActivity.this, R.string.text_fail,
                        Toast.LENGTH_SHORT).show();
                storeRusult(false);
            }
            mHandler.removeCallbacks(runnable);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, true);   //Turn ON
                Log.i(TAG, "turn on flash light");
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        mHandler.postDelayed(runnable, TIMEOUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            camManager.setTorchMode(cameraId, false);
            Log.i(TAG, "turn off flash light");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
