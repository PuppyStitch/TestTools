package com.sprd.validationtools.itemstest.tp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.itemstest.wholetest.BuzzerTestActivity;

public class ScreenTestActivity extends BaseActivity {

    private static final String TAG = "TouchTestActivity";

    FirstTouchView firstTouchView;
    SecondTouchView secondTouchView;

    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 120000;
    private boolean isOk = false;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(ScreenTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(ScreenTestActivity.this, R.string.text_fail,
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
            isOk = true;
            mHandler.post(runnable);
        });
        mHandler.postDelayed(runnable, TIMEOUT);            // 超过120s没有成功，就判定为失败
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
    }
}
