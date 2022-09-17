
package com.sprd.validationtools.itemstest.rtc;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;
import com.simcom.testtools.R;

public class RTCTest extends BaseActivity
{
    private static final String TAG = "RTCTest";
    TextView mContent;
    public static final String timeFormat = "hh:mm:ss";
    //public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(timeFormat);
    public long mTime;
    public Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContent = new TextView(this);
        mContent.setGravity(Gravity.CENTER);
        setContentView(mContent);
        setTitle(R.string.rtc_test);
        mTime = System.currentTimeMillis();
        setTimeText();
    }

    private void setTimeText() {
        mContent.postDelayed(new Runnable() {
            public void run() {
                mContent.setText(getResources().getText(R.string.rtc_tag) + getTime());
                mContent.setTextSize(35);
                if (System.currentTimeMillis() - mTime > 3000) {
                }
                else {
                    setTimeText();
                }
            }
        }, 100);
    }

    private String getTime() {
        SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(timeFormat);
        return TIME_FORMAT.format(new Date());
    }
}
