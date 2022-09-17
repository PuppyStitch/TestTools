
package com.sprd.validationtools.itemstest.led;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.PhaseCheckParse;
import com.simcom.testtools.R;

public class BlueLightTest extends BaseActivity {
    private static final String TAG = "BlueLightTest";
    private TextView mContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContent = new TextView(this);
        setBackground();
        setContentView(mContent);
        mContent.setGravity(Gravity.CENTER);
        mContent.setTextSize(35);
        boolean res = PhaseCheckParse.getInstance().writeLedlightSwitch(8, 1);
        Log.d(TAG, "startTestWork BlueLightTest res="+res);
    }

    private void setBackground() {
        mContent.setBackgroundColor(Color.BLUE);
        mContent.setText(getString(R.string.status_indicator_blue));
    }

    @Override
    protected void onDestroy() {
        PhaseCheckParse.getInstance().writeLedlightSwitch(8, 0);
        super.onDestroy();
    }

}
