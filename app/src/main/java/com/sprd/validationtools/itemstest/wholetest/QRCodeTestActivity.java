package com.sprd.validationtools.itemstest.wholetest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

public class QRCodeTestActivity extends BaseActivity {

    private static final String TAG = "QRCodeTestActivity";

    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 20000;
    private boolean isOk = false;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(QRCodeTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(QRCodeTestActivity.this, R.string.text_fail,
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
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            isOk = true;
//            Toast.makeText(this, "扫描内容:" + result.getContents(), Toast.LENGTH_LONG).show();
        }
    }

}

