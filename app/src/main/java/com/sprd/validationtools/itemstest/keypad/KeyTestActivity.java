package com.sprd.validationtools.itemstest.keypad;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;
import com.simcom.testtools.R;
import com.sprd.validationtools.utils.RootCmdUtils;
import com.sprd.validationtools.utils.ValidationToolsUtils;

public class KeyTestActivity extends BaseActivity {
    private static final String TAG = "KeyTestActivity";
    private ImageButton mHomeButton;
    private ImageButton mMenuButton;
    private ImageButton mBackButton;
    private ImageButton mPowerButton;
    private ImageButton mVolumeUpButton;
    private ImageButton mVolumeDownButton;
    private ImageButton mCameraButton;
    private byte keyPressedFlag = 0;
    private byte keySupportFlag = 0;
    private boolean isHideCamera = false;
    private boolean mHasPhysicalNavigationKey = true;
    private boolean isShowNavigationBar = false;
    private Context mContext;
    public Handler mHandler = new Handler();
    public boolean shouldBack = false;

    private HashMap<Integer, Button> mSupportkeys = new HashMap<>();
    private int mSupportKeyCount = 0;
    private int mPressedKeyCount = 0;
    private static final int COLUMNCOUNT = 4;
    private static final long TEST_TIMEOUT = 60000;

    boolean isHomePressed = false, isBackPressed = false, isRecentPressed = false,
            isPowerPressed = false, isResetPressed = false;

    private PressKeyBroadcastReceiver pressKeyBroadcastReceiver;
    private final String ACTION_TESTING_POWER_KEY = "action.press.powerbutton";
    private final String ACTION_TESTING_SWITCH_KEY = "action.press.switchbutton";
    private final String ACTION_TESTING_HOME_KEY = "action.press.homebutton";

//    private final String ECHO_LOCK_UP_KERNEL_ADDRESS = "echo 0x40388eee > /sys/kernel/debug/lookat/addr_rwpv";
//    private final String ECHO_FREE_KERNEL_ADDRESS = "echo 0x40388eec > /sys/kernel/debug/lookat/addr_rwpv";
    private final String ECHO_SET_RESET_KEY_TO_VOLUME_UP = "echo 1 > /sys/devices/platform/charger/vol_rst_key";
    private final String ECHO_SET_VOLUME_UP_TO_RESET_UP = "echo 0 > /sys/devices/platform/charger/vol_rst_key";

    String[] openCMD = new String[1];
    String[] closeCMD = new String[1];

    private final String SET_RESET_KEY_TO_VOLUME_UP = "lookat -s 0x374 0x40388eec";
    private final String SET_VOLUME_UP_TO_RESET_UP = "lookat -s 0x37c 0x40388eec";
    private final String REBOOT = "input keyevent 24\n";

    private Button mAiButton = null;
    private Button mResetButton = null;

    private void initView(Context context) {
        Resources res = getResources();
        // ScrollView scrollView = new ScrollView(context);
        GridLayout gridLayout = new GridLayout(context);
        gridLayout.setColumnCount(COLUMNCOUNT);
        gridLayout.setFocusable(false);
        // scrollView.addView(gridLayout);

        DisplayMetrics dm = res.getDisplayMetrics();
        int width = dm.widthPixels;

        String[] supportkeyNames = res
                .getStringArray(R.array.keyname_support_list);
        int[] supportkeyCodes = res.getIntArray(R.array.keycode_support_list);
        for (String supportkeyName : supportkeyNames) {
            Log.d(TAG, "initView supportkeyName=" + supportkeyName);
        }
        int i = 0;
        for (int supportkeyCode : supportkeyCodes) {
            Log.d(TAG, "initView supportkeyCode=" + supportkeyCode);
            if (i >= supportkeyNames.length) {
                return;
            }
            Button button = new Button(context);
            button.setWidth(width / COLUMNCOUNT);
            button.setPadding(2, 2, 2, 2);
            button.setText(supportkeyNames[i++]);
            button.setFocusable(false);
            // button.setTextColor(Color.RED);
            gridLayout.addView(button);
            mSupportkeys.put(supportkeyCode, button);
        }
        mSupportKeyCount = supportkeyCodes.length;
        mPressedKeyCount = 0;
        Log.d(TAG, "initView mSupportKeyCount=" + mSupportKeyCount
                + ",mPressedKeyCount=" + mPressedKeyCount);
        setContentView(gridLayout);

        removeButton();
        //
        mHandler.postDelayed(mTimeOutRunnable, TEST_TIMEOUT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        openCMD[0] = ECHO_LOCK_UP_KERNEL_ADDRESS;
        openCMD[0] = ECHO_SET_RESET_KEY_TO_VOLUME_UP;

        closeCMD[0] = ECHO_SET_VOLUME_UP_TO_RESET_UP;
//        closeCMD[1] = ECHO_FREE_KERNEL_ADDRESS;

        RootCmdUtils.echo(openCMD);

        mContext = getApplicationContext();
        if (Const.isSupportFeaturePhone()) {
            initView(this);
        } else {
            setContentView(R.layout.key_test);
            setTitle(R.string.key_test);
            mHomeButton = (ImageButton) findViewById(R.id.home_button);
            mMenuButton = (ImageButton) findViewById(R.id.menu_button);
            mBackButton = (ImageButton) findViewById(R.id.back_button);
            mPowerButton = (ImageButton) findViewById(R.id.power_button);
            mVolumeUpButton = (ImageButton) findViewById(R.id.volume_up_button);
            mVolumeDownButton = (ImageButton) findViewById(R.id.volume_down_button);
            mCameraButton = (ImageButton) findViewById(R.id.camera_button);
            mResetButton = (Button) findViewById(R.id.reset_button);
            isShowNavigationBar = ValidationToolsUtils.hasNavigationBar(this);
            mHasPhysicalNavigationKey = checkDeviceHasNavigationBar(mContext) && !isShowNavigationBar;
//            showHasCameraDialog();
            /* SPRD bug 760913:Test can pass/fail must click button */
            if (Const.isBoardISharkL210c10()) {
                mPassButton.setVisibility(View.GONE);
            }
            /* @} */
//            if (isShowNavigationBar) {
//                Log.i(TAG, "button is invisible");
//                mPassButton.setVisibility(View.GONE);
//                mFailButton.setVisibility(View.GONE);
//            }
//            mAiButton = (Button) findViewById(R.id.reset_button);
//            mAiButton.setVisibility(View.GONE);
        }
        registerPressKeyReceiver();
        isHomePressed = false;
        isBackPressed = false;
        isRecentPressed = false;
        isPowerPressed = false;
        isResetPressed = false;

        mResetButton.setOnClickListener(v -> {
//            String res = RootCmdUtils.execRootCmd(SET_RESET_KEY_TO_VOLUME_UP);
//            Log.i(TAG, );
        });

//        RootCmdUtils.execRootCmd("dumpsys window | grep mCurrentFocus");

//        RootCmdUtils.execRootCmd(SET_RESET_KEY_TO_VOLUME_UP);
    }

    @Override
    public void onResume() {
        super.onResume();
        disablePassButton();
//        if (isShowNavigationBar) {
//            hideNavigationBar();
//        }
    }

    private void needToEnablePassBtn() {
        if (isHomePressed && isBackPressed && isRecentPressed && isPowerPressed && isResetPressed) {
            enablePassButton();
        }
    }

    public boolean checkDeviceHasNavigationBar(Context context) {
        // boolean hasMenuKey =
        // ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);
        Log.d(TAG, "hasBackKey=:" + hasBackKey);
        if (hasBackKey) {
            return true;
        }
        return false;
    }

    protected void showHasCameraDialog() {
        Dialog cameraKeyDialog = new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(this.getString(R.string.has_camera_title))
                .setCancelable(false)
                .setNegativeButton(R.string.has_camera_key,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                isHideCamera = false;
                                showKey(isHideCamera);
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(R.string.no_camera_key,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                isHideCamera = true;
                                showKey(isHideCamera);
                                dialog.dismiss();
                            }
                        }).create();
        cameraKeyDialog.show();
    }

    private void showKey(boolean hideCamera) {

        mHomeButton.setVisibility(View.VISIBLE);
        keySupportFlag |= 1;

        mBackButton.setVisibility(View.VISIBLE);
        keySupportFlag |= 2;

        mMenuButton.setVisibility(View.VISIBLE);
        keySupportFlag |= 4;

//        if (Const.isHomeSupport(this) && mHasPhysicalNavigationKey) {
//            mHomeButton.setVisibility(View.VISIBLE);
//            keySupportFlag |= 1;
//        } else {
//            /*
//             * SPRD: modify 20140529 Spreadtrum of 305634 MMI test,lack of
//             * button which is on the right 0f "Home" @{
//             */
//            mHomeButton.setVisibility(View.GONE);
//            /* @} */
//        }
//        if (Const.isBackSupport(this) && mHasPhysicalNavigationKey) {
//            mBackButton.setVisibility(View.VISIBLE);
//            keySupportFlag |= 2;
//        } else {
//            /*
//             * SPRD: modify 20140529 Spreadtrum of 305634 MMI test,lack of
//             * button which is on the right 0f "Home" @{
//             */
//            mBackButton.setVisibility(View.GONE);
//            /* @} */
//        }
//        if (Const.isMenuSupport(this) && mHasPhysicalNavigationKey) {
//            mMenuButton.setVisibility(View.VISIBLE);
//            keySupportFlag |= 4;
//        } else {
//            mMenuButton.setVisibility(View.GONE);
//        }
//        if (Const.isCameraSupport() && !hideCamera) {
//            mCameraButton.setVisibility(View.VISIBLE);
//            keySupportFlag |= 8;
//        } else if (hideCamera) {
//            mCameraButton.setVisibility(View.GONE);
//        }
//        if (Const.isVolumeUpSupport()) {
//            mVolumeUpButton.setVisibility(View.VISIBLE);
//            keySupportFlag |= 16;
//        }
//        if (Const.isVolumeDownSupport()) {
//            mVolumeDownButton.setVisibility(View.VISIBLE);
//            keySupportFlag |= 32;
//        }
//        if(ValidationToolsUtils.isSupportAIKey()){
//            mAiButton.setVisibility(View.VISIBLE);
//            keySupportFlag |= 64;
//        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown keyCode=" + keyCode);
        if (Const.isSupportFeaturePhone()) {
            Button button = mSupportkeys.get(keyCode);
            if (button != null) {
                Log.d(TAG, " onKeyDown is support key!");
                if (!button.isPressed()) {
                    button.setPressed(true);
                    mPressedKeyCount++;
                }
                Log.d(TAG, "onKeyDown mPressedKeyCount=" + mPressedKeyCount
                        + ",mSupportKeyCount=" + mSupportKeyCount);
                if (mPressedKeyCount == mSupportKeyCount) {
                    storeRusult(true);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }
            }
            return true;
        }
        if (KeyEvent.KEYCODE_HOME == keyCode) {
            mHomeButton.setPressed(true);
            keyPressedFlag |= 1;
        } else if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (!mBackButton.isPressed()) {
                mBackButton.setPressed(true);
                mBackButton.setBackgroundColor(Color.GREEN);
                isBackPressed = true;
                needToEnablePassBtn();
                keyPressedFlag |= 2;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else if (KeyEvent.KEYCODE_APP_SWITCH == keyCode || keyCode == 999) {
            mMenuButton.setPressed(true);
            keyPressedFlag |= 4;
        } else if (KeyEvent.KEYCODE_CAMERA == keyCode) {
            mCameraButton.setPressed(true);
            keyPressedFlag |= 8;
        } else if (KeyEvent.KEYCODE_VOLUME_UP == keyCode) {
//            mVolumeUpButton.setPressed(true);
//            keyPressedFlag |= 16;
            mResetButton.setPressed(true);
            isResetPressed = true;
            mResetButton.setBackgroundColor(Color.GREEN);
            needToEnablePassBtn();
        } else if (KeyEvent.KEYCODE_VOLUME_DOWN == keyCode) {
            mVolumeDownButton.setPressed(true);
            keyPressedFlag |= 32;
        }
        //AI keyCode == 766
        else if (766 == keyCode) {
            mAiButton.setPressed(true);
            keyPressedFlag |= 64;
        }
//        if (keySupportFlag == keyPressedFlag) {
//            BaseActivity.shouldCanceled = false;
//            // showResultDialog(getString(R.string.key_test_info));
//            /* SPRD bug 760913:Test can pass/fail must click button */
//            if (!Const.isBoardISharkL210c10()) {
//                storeRusult(true);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                    /* SPRD bug 752003:Avoid press home key go to launcher */
//                }, 1000);
//            } else {
//                /* SPRD bug 760913:Test can pass/fail must click button */
//                mPassButton.setVisibility(View.VISIBLE);
//            }
//            if (isShowNavigationBar) {
//                storeRusult(true);
//                Toast.makeText(KeyTestActivity.this, R.string.text_pass,
//                        Toast.LENGTH_SHORT).show();
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                }, 1000);
//            }
//        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mTimeOutRunnable);
        unregisterReceiver(pressKeyBroadcastReceiver);
//        String res = RootCmdUtils.execRootCmd(SET_VOLUME_UP_TO_RESET_UP);
//        Log.d(TAG, res);
        super.onDestroy();

        RootCmdUtils.echo(closeCMD);
    }

    public Runnable mTimeOutRunnable = new Runnable() {
        public void run() {
            storeRusult(false);
            Toast.makeText(KeyTestActivity.this, R.string.text_fail,
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    public void registerPressKeyReceiver() {
        pressKeyBroadcastReceiver = new PressKeyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TESTING_POWER_KEY);
        filter.addAction(ACTION_TESTING_SWITCH_KEY);
        filter.addAction(ACTION_TESTING_HOME_KEY);
        registerReceiver(pressKeyBroadcastReceiver, filter);
    }

    class PressKeyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_TESTING_POWER_KEY.equals(action)) {
                mPowerButton.setBackgroundColor(Color.GREEN);
                isPowerPressed = true;
                Log.i(TAG, "POWER KEY PRESSED");
            } else if (ACTION_TESTING_SWITCH_KEY.equals(action)) {
                mMenuButton.setBackgroundColor(Color.GREEN);
                isRecentPressed = true;
                Log.i(TAG, "SWITCH KEY PRESSED");
            } else if (ACTION_TESTING_HOME_KEY.equals(action)) {
                Log.i(TAG, "HOME KEY PRESSED");
                mHomeButton.setBackgroundColor(Color.GREEN);
                isHomePressed = true;
            }
            needToEnablePassBtn();
        }
    }
}
