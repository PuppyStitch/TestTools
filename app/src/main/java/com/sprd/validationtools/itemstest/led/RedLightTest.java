
package com.sprd.validationtools.itemstest.led;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.PhaseCheckParse;
import com.simcom.testtools.R;

public class RedLightTest extends BaseActivity {
    private static final String TAG = "RedLightTest";
    private TextView mContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContent = new TextView(this);
        setBackground();
        setContentView(mContent);
        mContent.setGravity(Gravity.CENTER);
        mContent.setTextSize(35);
        PhaseCheckParse.getInstance().writeLedlightSwitch(7, 1);
    }

    private void setBackground() {
        mContent.setBackgroundColor(Color.RED);
        mContent.setText(getString(R.string.status_indicator_red));
    }

    @Override
    protected void onDestroy() {
    	PhaseCheckParse.getInstance().writeLedlightSwitch(7, 0);
        super.onDestroy();
    }
}
