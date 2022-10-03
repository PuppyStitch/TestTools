package com.sprd.validationtools.itemstest.sptest;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

public class POSSensorTestActivity extends BaseActivity {

    private static final String TAG = "POSSensorTestActivity";

    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 16000;
    private boolean isOk = false;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(POSSensorTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(POSSensorTestActivity.this, R.string.text_fail,
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
        disablePassButton();
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
}
