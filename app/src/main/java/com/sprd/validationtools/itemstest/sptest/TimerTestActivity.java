package com.sprd.validationtools.itemstest.sptest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTestActivity extends BaseActivity {

    private static final String TAG = "TimerTestActivity";

    TextView mContent;
    private PendingIntent mPendingIntent;
    MyHandler myHandler;
    final int MSG_RESULT_SUCCESS = 0;
    final int MSG_RESULT_FAILED = 1;
    final int MSG_UPDATE_TIMER = 2;

    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 16000;
    private boolean isOk = true;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(TimerTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(TimerTestActivity.this, R.string.text_fail,
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContent = new TextView(this);
        mContent.setGravity(Gravity.CENTER);
        mContent.setTextSize(20);
        setTitle("Timer");
        setContentView(mContent);
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        Log.d(TAG, "onCreate");
        myHandler = new MyHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTimerText();
        Message msg = new Message();
        msg.what = MSG_RESULT_SUCCESS;
        myHandler.sendMessageDelayed(msg, 1000);
        mHandler.postDelayed(runnable, TIMEOUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
    }

    private void setTimerText() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = format.format(curDate);
        mContent.setText(str);
        isOk = true;
    }

    public class MyHandler extends Handler {
        WeakReference<Activity> activityWeakReference;

        MyHandler(Activity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_RESULT_SUCCESS:
//                    storeResult(msg.what == MSG_RESULT_SUCCESS);
                    break;
                case MSG_UPDATE_TIMER:
                    setTimerText();
                    break;
                default:
                    break;
            }

        }
    }
}
