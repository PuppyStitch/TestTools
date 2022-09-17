package com.sprd.validationtools.itemstest.sptest;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;

public class BuzzerTestActivity extends BaseActivity {

    private static final String TAG = "BuzzerTestActivity";

    TextView mContent;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContent = new TextView(this);
        mContent.setGravity(Gravity.CENTER);
        mContent.setTextSize(20);
        setTitle("Buzzer");
        setContentView(mContent);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
