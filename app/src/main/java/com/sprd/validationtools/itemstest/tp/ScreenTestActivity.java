package com.sprd.validationtools.itemstest.tp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

public class ScreenTestActivity extends BaseActivity {

    private static final String TAG = "TouchTestActivity";

    FirstTouchView firstTouchView;
    SecondTouchView secondTouchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_finger_print_test);

        firstTouchView = findViewById(R.id.first_touch_view);
        secondTouchView = findViewById(R.id.second_touch_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPassButton.setVisibility(View.INVISIBLE);
        mFailButton.setVisibility(View.INVISIBLE);

        firstTouchView.setTouchResultListener(isPass -> {
            firstTouchView.setVisibility(View.GONE);
            secondTouchView.setVisibility(View.VISIBLE);
        });

        secondTouchView.setTouchResultListener(isPass -> {
            Log.i(TAG, "secondTouchView pass");
            mPassButton.setVisibility(View.VISIBLE);
            mFailButton.setVisibility(View.VISIBLE);
        });
    }

}
