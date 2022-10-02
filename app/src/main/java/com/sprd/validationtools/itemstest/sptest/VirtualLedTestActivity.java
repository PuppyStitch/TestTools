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
        mStartButton.setText(getResources().getText(R.string.turn_on_lights));
        mStartButton.setOnClickListener(view -> start());
        mStopButton.setText(getResources().getText(R.string.turn_off_lights));
        mStopButton.setOnClickListener(view -> stop());

        activityManager = (ActivityManager)
                this.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void start() {
        try {
            activityManager.showBlue(true);
            activityManager.showYellow(true);
            activityManager.showGreen(true);
            activityManager.showRed(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        try {
            activityManager.showBlue(false);
            activityManager.showYellow(false);
            activityManager.showGreen(false);
            activityManager.showRed(false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
