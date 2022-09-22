package com.sprd.validationtools.itemstest.backlight;

import java.util.Timer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.itemstest.lcd.ScreenColorTest;
import com.sprd.validationtools.utils.ValidationToolsUtils;

public class BackLightTest extends BaseActivity implements OnClickListener {
    private static final String TAG = "BackLightTest";
    PowerManager mPowerManager = null;
    TextView mContent;
    private static final int[] COLOR_ARRAY = new int[]{Color.WHITE,
            Color.BLACK};
    private boolean isShowNavigationBar = false;
    private int mIndex = 0, mCount = 0;
    Timer mTimer;
    private static final int TIMES = 5;
    private Handler mUiHandler = new Handler();
    ;
    private Runnable mRunnable;

    protected RelativeLayout mRelativeLayout;
    protected Button passButton;
    protected Button failButton;

    /*SPRD bug 839657:Change screen light*/
    private static final int MAX_BRIGHTNESS = 255;
    private static final boolean TEST_SCREEN_LIGHT = true;

    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 10000;
    private boolean isOk = true;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(BackLightTest.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(BackLightTest.this, R.string.text_fail,
                        Toast.LENGTH_SHORT).show();
                storeRusult(false);
            }
            mHandler.removeCallbacks(runnable);
            finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        isShowNavigationBar = ValidationToolsUtils.hasNavigationBar(this);
        if (isShowNavigationBar) {
            setContentView(R.layout.background_layout);
            mRelativeLayout = (RelativeLayout) findViewById(R.id.background_relativelayout);
            passButton = (Button) findViewById(R.id.pass_btn);
            failButton = (Button) findViewById(R.id.fail_btn);
            passButton.setOnClickListener(this);
            failButton.setOnClickListener(this);
        } else {
            mContent = new TextView(this);
            setContentView(mContent);
        }
        setTitle(R.string.backlight_test);
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mRunnable = new Runnable() {
            public void run() {
                if (TEST_SCREEN_LIGHT) {
                    startScreenLight();
                    mCount++;
                } else {
                    if (isShowNavigationBar) {
                        mRelativeLayout.setBackgroundColor(COLOR_ARRAY[mIndex]);
                    } else {
                        mContent.setBackgroundColor(COLOR_ARRAY[mIndex]);
                    }
                    mIndex = 1 - mIndex;
                    mCount++;
                }
                if (isShowNavigationBar) {
                    mPassButton.setVisibility(View.GONE);
                    mFailButton.setVisibility(View.GONE);
                }
                setBackground();
            }
        };
        setBackground();
    }

    @Override
    public void onClick(View v) {
        if (isShowNavigationBar) {
            if (v == passButton) {
                storeRusult(true);
                finish();
            } else if (v == failButton) {
                storeRusult(false);
                finish();
            }
        } else {
            if (v == mPassButton) {
                storeRusult(true);
                finish();
            } else if (v == mFailButton) {
                storeRusult(false);
                finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowNavigationBar) {
            mPassButton.setVisibility(View.GONE);
            mFailButton.setVisibility(View.GONE);
            hideNavigationBar();
        }
        mHandler.postDelayed(runnable, TIMEOUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
    }

    private void setScreenLight(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        context.getWindow().setAttributes(lp);
    }

    private void startScreenLight() {
        try {
            if (isShowNavigationBar) {
                mRelativeLayout.setBackgroundColor(Color.WHITE);
            } else {
                mContent.setBackgroundColor(Color.WHITE);
            }
            setScreenLight(BackLightTest.this, MAX_BRIGHTNESS >> mCount);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setBackground() {
        if (mCount > TIMES) {
            if (TEST_SCREEN_LIGHT) {
                setScreenLight(BackLightTest.this, MAX_BRIGHTNESS / 2);
            }
            if (isShowNavigationBar) {
                passButton.setVisibility(View.VISIBLE);
                failButton.setVisibility(View.VISIBLE);
            }
            return;
        }
        mUiHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void onDestroy() {
        mUiHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }
}
