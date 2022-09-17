package com.sprd.validationtools.itemstest.fm;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;
import com.simcom.testtools.R;

public class FMTest extends BaseActivity {
    private static final String TAG = "FMTest";
    private TextView mContent = null;

    private static final String SYSTEM_FMRADIO_PACKAGE = "com.android.fmradio";
    private static final String SYSTEM_FMRADIO_ACTIVITY = "com.android.fmradio.FmMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContent = new TextView(this);
        mContent.setGravity(Gravity.CENTER);
        mContent.setText(getString(R.string.fm_test));
        setContentView(mContent);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        staFmRadioIntent();
    }

    private void staFmRadioIntent(){
        try {
            Intent intent = new Intent();
            intent.setClassName(SYSTEM_FMRADIO_PACKAGE, SYSTEM_FMRADIO_ACTIVITY);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
