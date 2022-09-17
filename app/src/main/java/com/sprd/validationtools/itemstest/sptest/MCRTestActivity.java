package com.sprd.validationtools.itemstest.sptest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;

import java.lang.ref.WeakReference;

public class MCRTestActivity extends BaseActivity {

    private Button mStartButton;
    private MyHandler myHandler;

    private static final String TAG = "MSRTestActivity";

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
        barcodeLayout.addView(mStartButton);
//        barcodeLayout.addView(mStopButton);
        setContentView(barcodeLayout);
        setTitle("MCR");
        mStartButton.setText(getResources().getText(R.string.start_play));
        mStartButton.setOnClickListener(view -> start());
        myHandler = new MyHandler(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
    }

    private void start() {
        Intent intent = new Intent();
        intent.setClassName("com.example.myprinterdemo", "com.example.myprinterdemo.CITTest.MyMCRActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 100);
    }

    private void storeResult(boolean isSuccess) {
        storeRusult(isSuccess);
    }

    public class MyHandler extends Handler {
        WeakReference<Activity> activityWeakReference;

        MyHandler(Activity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            storeResult(msg.what == Const.SUCCESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            myHandler.sendEmptyMessageDelayed(resultCode, 1000);
        }
    }
}
