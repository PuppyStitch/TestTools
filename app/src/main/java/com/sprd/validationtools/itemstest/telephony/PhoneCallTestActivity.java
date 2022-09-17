
package com.sprd.validationtools.itemstest.telephony;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;
import com.simcom.testtools.R;

public class PhoneCallTestActivity extends BaseActivity {
    private TextView mContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContent = new TextView(this);
        mContent.setGravity(Gravity.CENTER);
        mContent.setText(getString(R.string.phone_test_title));
        mContent.setTextSize(25);
        setContentView(mContent);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        //showResultDialog(getString(R.string.phone_call_info));
        Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", Uri.parse("tel:112"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("factory_mode", true);
        startActivity(intent);
    }
}
