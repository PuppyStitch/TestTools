package com.sprd.validationtools.itemstest.sptest;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;


public class VirtualLedTestActivity extends BaseActivity {

    private static final String TAG = "VirtualLedTestActivity";

    TextView mContent;
    private PendingIntent mPendingIntent;
    private Button mStartButton, mStopButton;
    ActivityManager activityManager;

    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 20000;
    private boolean isOk = false;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(VirtualLedTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(VirtualLedTestActivity.this, R.string.text_fail,
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
        setTitle("VirtualLedTestActivity");
        mStartButton.setText(getResources().getText(R.string.start_play));
        mStartButton.setOnClickListener(view -> start());
        mStopButton.setText(getResources().getText(R.string.stop_play));
        mStopButton.setOnClickListener(view -> stop());

        activityManager = (ActivityManager)
                this.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(runnable, TIMEOUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
    }

    private void start() {
        try {
            activityManager.showBlue(true);
            activityManager.showYellow(true);
            activityManager.showGreen(true);
            activityManager.showRed(true);
            isOk = true;
        } catch (RemoteException e) {
            e.printStackTrace();
            isOk = false;
        }
    }

    private void stop() {
        try {
            activityManager.showBlue(false);
            activityManager.showYellow(false);
            activityManager.showGreen(false);
            activityManager.showRed(false);
            isOk = true;
        } catch (RemoteException e) {
            e.printStackTrace();
            isOk = false;
        }
    }
}
