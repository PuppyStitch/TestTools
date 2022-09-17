package com.sprd.validationtools.itemstest.wholetest;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

public class PrintTestActivity extends BaseActivity {

    private static final String TAG = "PrintTestActivity";

    private Button mButton;
    private final int REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout printLayout = new LinearLayout(this);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        printLayout.setLayoutParams(params);
        printLayout.setOrientation(1);
        printLayout.setGravity(Gravity.CENTER);
        mButton = new Button(this);
        mButton.setTextSize(35);
        printLayout.addView(mButton);
        setContentView(printLayout);
        setTitle(R.string.print_test);
        mButton.setText(getResources().getText(R.string.print_test));
        mButton.setOnClickListener(view -> start());
    }

    public void start() {
        //send broadcast to execute printing
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.example.myprinterdemo", "com.example.myprinterdemo.service.PrinterBroadcastReceiver");
        intent.setComponent(componentName);
        intent.putExtra("command", 1);
        sendBroadcast(intent);
    }

}
