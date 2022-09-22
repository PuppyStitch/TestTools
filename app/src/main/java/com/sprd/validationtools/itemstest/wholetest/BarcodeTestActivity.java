package com.sprd.validationtools.itemstest.wholetest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.itemstest.wifi.WifiTestActivity;

public class BarcodeTestActivity extends BaseActivity {

    private static final String TAG = "BarcodeTestActivity";

    public Handler mHandler = new Handler();

    private static final int TIMEOUT = 20000;

    private boolean isOk = false;

    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(BarcodeTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(BarcodeTestActivity.this, R.string.text_fail,
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
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setTimeout(TIMEOUT - 1000);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(runnable, TIMEOUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "the result is " + result);
        if (result != null) {
            isOk = true;
            Toast.makeText(this, "扫描内容:" + result.getContents(), Toast.LENGTH_LONG).show();
        }
    }

}

