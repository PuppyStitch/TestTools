package com.sprd.validationtools.itemstest.wholetest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

import static android.os.BatteryManager.EXTRA_LEVEL;
import static android.os.BatteryManager.EXTRA_VOLTAGE;

public class BatteryTestActivity extends BaseActivity {

    private int mVoltage, mLevel;
    private TextView mVolTV, mLevTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout batteryLayout = new LinearLayout(this);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        batteryLayout.setLayoutParams(params);
        batteryLayout.setOrientation(1);
        batteryLayout.setGravity(Gravity.CENTER);
        mVolTV = new TextView(this);
        mLevTV = new TextView(this);
        mVolTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mLevTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mVolTV.setTextSize(35);
        mLevTV.setTextSize(35);
        batteryLayout.addView(mVolTV);
        batteryLayout.addView(mLevTV);
        setContentView(batteryLayout);
        setTitle(R.string.battery_test);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }

            mVoltage = intent.getIntExtra(EXTRA_VOLTAGE, -1);
            mLevel = intent.getIntExtra(EXTRA_LEVEL, 0);

            updateUI();
        }
    };

    private void updateUI() {
        mVolTV.setText(mVoltage + "mV");
        mLevTV.setText(mLevel + "%");
    }

}
