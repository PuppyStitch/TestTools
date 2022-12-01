
package com.sprd.validationtools.itemstest.lcd;

import android.content.Context;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;
import com.simcom.testtools.R;

public class ScreenColorTest extends BaseActivity implements OnClickListener {
    private String TAG = "ScreenColorTest";
    TextView mContent;

    protected RelativeLayout mRelativeLayout;
    protected Button passButton;
    protected Button failButton;

    int mIndex = 0, mCount = 0;
    private boolean isShowNavigationBar = false;
    private Handler mUiHandler = new Handler();
    private Runnable mRunnable;

    private static final int[] COLOR_ARRAY = new int[] {
            Color.RED, Color.GREEN, Color.BLUE, Color.WHITE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        isShowNavigationBar = ValidationToolsUtils.hasNavigationBar(this);
        isShowNavigationBar = true;
        Log.d(TAG, "isShowNavigationBar=:" + isShowNavigationBar);
        if (isShowNavigationBar) {
            setContentView(R.layout.background_layout);
            mRelativeLayout = (RelativeLayout) findViewById(R.id.background_relativelayout);
            passButton = (Button) findViewById(R.id.pass_btn);
            failButton = (Button) findViewById(R.id.fail_btn);
            passButton.setOnClickListener(this);
            failButton.setOnClickListener(this);
            passButton.setVisibility(View.GONE);
            failButton.setVisibility(View.GONE);
        } else {
            mContent = new TextView(this);
            mContent.setGravity(Gravity.CENTER);
            mContent.setTextSize(35);
            setContentView(mContent);
        }
        mRunnable = new Runnable() {
            public void run() {
                if (isShowNavigationBar) {
                    mRelativeLayout.setBackgroundColor(COLOR_ARRAY[mIndex]);
                } else {
                    mContent.setBackgroundColor(COLOR_ARRAY[mIndex]);
                }
                mIndex++;
                mCount++;
                setBackground();
            }
        };
        mPassButton.setVisibility(View.GONE);
        mFailButton.setVisibility(View.GONE);
        setBackground();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowNavigationBar) {
            mPassButton.setVisibility(View.GONE);
            mFailButton.setVisibility(View.GONE);
            hideNavigationBar();
        }

        showSecondByDisplayManager(getApplicationContext());
    }

    private void showSecondByDisplayManager(Context context) {
        DisplayManager mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        if (displays != null) {
            BasePresentation presentation = new BasePresentation(context, displays[displays.length - 1]);
            presentation.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    private void setBackground() {
        if (mIndex >= COLOR_ARRAY.length) {
            if (isShowNavigationBar) {
                passButton.setVisibility(View.VISIBLE);
                failButton.setVisibility(View.VISIBLE);
                hideNavigationBar();
            } else {
                mPassButton.setVisibility(View.VISIBLE);
                mFailButton.setVisibility(View.VISIBLE);
            }
            return;
        }

        if (isShowNavigationBar) {
            mRelativeLayout.setBackgroundColor(COLOR_ARRAY[mIndex]);
        } else {
            mContent.setBackgroundColor(COLOR_ARRAY[mIndex]);
        }

        /* SPRD Bug 771296:LCD screen test, white screen continue 3 seconds. @{ */
        if (Const.isBoardISharkL210c10()) {
            if (mIndex == 0) {
                mUiHandler.postDelayed(mRunnable, 3000);
            } else {
                mUiHandler.postDelayed(mRunnable, 1000);
            }
        } else {
            mUiHandler.postDelayed(mRunnable, 600);
        }
        /* @} */
    }

    @Override
    public void onDestroy() {
        mUiHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }
}
