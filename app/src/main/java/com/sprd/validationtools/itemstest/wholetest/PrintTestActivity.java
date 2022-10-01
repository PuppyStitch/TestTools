package com.sprd.validationtools.itemstest.wholetest;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.testinfo.TestInfoMainActivity;

public class PrintTestActivity extends BaseActivity {

    private static final String TAG = "PrintTestActivity";

//    private Button mButton;
    private final int REQUEST_CODE = 102;

    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 8000;
    private boolean isOk = true;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(PrintTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(PrintTestActivity.this, R.string.text_fail,
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
        LinearLayout printLayout = new LinearLayout(this);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        printLayout.setLayoutParams(params);
        printLayout.setOrientation(LinearLayout.VERTICAL);
        printLayout.setGravity(Gravity.CENTER);
//        mButton = new Button(this);
//        mButton.setTextSize(35);
//        printLayout.addView(mButton);
        setContentView(printLayout);
        setTitle(R.string.print_test);
//        mButton.setText(getResources().getText(R.string.print_test));
        start();
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

    public void start() {
        //start an other to print
        try {
            Log.d(TAG, "go to print page");
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName("com.example.myprinterdemo", "com.example.myprinterdemo.ui.CitPrintTestActivity");
            intent.setComponent(componentName);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
