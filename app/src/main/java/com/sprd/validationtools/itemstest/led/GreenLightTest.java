
package com.sprd.validationtools.itemstest.led;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.PhaseCheckParse;
import com.simcom.testtools.R;

public class GreenLightTest extends BaseActivity {
    private static final String TAG = "GreenLightTest";
    private TextView mContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContent = new TextView(this);
        setBackground();
        setContentView(mContent);
        mContent.setGravity(Gravity.CENTER);
        mContent.setTextSize(35);
        PhaseCheckParse.getInstance().writeLedlightSwitch(9, 1);
    }

    private void setBackground() {
        mContent.setBackgroundColor(Color.GREEN);
        mContent.setText(getString(R.string.status_indicator_green));

    }

    @Override
    protected void onDestroy() {
        PhaseCheckParse.getInstance().writeLedlightSwitch(9, 0);
        super.onDestroy();
    }

}
