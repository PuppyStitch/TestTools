
package com.sprd.validationtools.itemstest.otg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sprd.validationtools.Const;
import com.sprd.validationtools.utils.StorageUtil;
import com.simcom.testtools.R;
import android.widget.Toast;
import android.os.Message;
import android.view.InputDevice;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import com.sprd.validationtools.BaseActivity;

public class OTGTest extends BaseActivity {
    private String TAG = "OTGTest";
    private TextView mTextView, mCountDownView;
    private StorageManager mStorageManager = null;
    private boolean isUsb = false;
    private String usbMassStoragePath = "/storage/usbdisk";
    private static final String SPRD_OTG_TESTFILE = "otgtest.txt";
    private String otgPath = null;
    public byte mOTGTestFlag[] = new byte[1];
    byte[] result = new byte[1];
    byte[] mounted = new byte[1];
    private static final int UPDATE_TIME = 0;
    private long time = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout OTGLayout = new LinearLayout(this);
        LayoutParams parms = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        OTGLayout.setLayoutParams(parms);
        OTGLayout.setOrientation(1);
        OTGLayout.setGravity(Gravity.CENTER);
        mTextView = new TextView(this);
        mTextView.setTextSize(35);
        mCountDownView = new TextView(this);
        mCountDownView.setTextSize(15);
        OTGLayout.addView(mTextView);
        OTGLayout.addView(mCountDownView);
        setContentView(OTGLayout);
        setTitle(R.string.otg_test);
        mTextView.setText(getResources().getText(R.string.otg_begin_test));
        mCountDownView.setText(time + "");
        mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
        /*SPRD bug 760913:Test can pass/fail must click button*/
        if(Const.isBoardISharkL210c10()){
            mPassButton.setVisibility(View.GONE);
        }
        /*@}*/
    }

    private void checkOTGdevices() {
        /* SPRD Bug 766333: OTG test should use mouse not USB. @{ */
        if (Const.isBoardISharkL210c10()) {
            final int[] devices = InputDevice.getDeviceIds();
            for (int i = 0; i < devices.length; i++) {
                InputDevice device = InputDevice.getDevice(devices[i]);
                if (device.getSources() == InputDevice.SOURCE_MOUSE) {
                    mounted[0] = 0;
                    Log.i(TAG, "=== OTG mount succeed ===");
                } else {
                    mounted[0] = 1;
                    Log.i(TAG, "=== OTG mount Fail ===");
                }
            }
        } else {
            /* SPRD Bug 940774:OTG test fail, because permission denied. @{ */
            String otgPath = StorageUtil.getExternalStorageAppPath(getApplicationContext(), Const.OTG_UDISK_PATH);
            if (otgPath != null) {
                mounted[0] = 0;
                Log.i(TAG, "=== OTG mount succeed ===");
                usbMassStoragePath = otgPath;
            } else {
                mounted[0] = 1;
                Log.i(TAG, "=== OTG mount Fail ===");
            }
            /* @} */
        }
        /* @} */
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
            case UPDATE_TIME:
                time--;
                mCountDownView.setText(time + "");
                if(time == 0) {
                    Log.d(TAG, "time out");
                    Toast.makeText(OTGTest.this, R.string.text_fail,
                            Toast.LENGTH_SHORT).show();
                    /*SPRD bug 760913:Test can pass/fail must click button*/
                    if(Const.isBoardISharkL210c10()){
                        Log.d("", "isBoardISharkL210c10 is return!");
                        return;
                    }
                    /*@}*/
                    storeRusult(false);
                    finish();
                } else {
                    mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
                }
                break;
            }
            super.handleMessage(msg);
        }
    };

    public Runnable mRunnable = new Runnable() {
        public void run() {
            Log.i(TAG, "=== display OTG test succeed info! ===");
            if (mounted[0] == 0) {
                if (result[0] == 0) {
                    mTextView.setText(getResources().getText(R.string.otg_test_success));
                    /*SPRD bug 760913:Test can pass/fail must click button*/
                    if(Const.isBoardISharkL210c10()){
                        Log.d("", "isBoardISharkL210c10 is return!");
                        mPassButton.setVisibility(View.VISIBLE);
                        return;
                    }
                    /*@}*/
                    storeRusult(true);
                    finish();
                } else {
                    mTextView.setText(getResources().getText(R.string.otg_test_fail));
                    /*SPRD bug 760913:Test can pass/fail must click button*/
                    if(Const.isBoardISharkL210c10()){
                        Log.d("", "isBoardISharkL210c10 is return!");
                        return;
                    }
                    /*@}*/
                    storeRusult(false);
                    finish();
                }
            } else {
                mTextView.setText(getResources().getText(R.string.otg_test_fail));
                /*SPRD bug 760913:Test can pass/fail must click button*/
                if(Const.isBoardISharkL210c10()){
                    Log.d("", "isBoardISharkL210c10 is return!");
                    return;
                }
                /*@}*/
                storeRusult(false);
                finish();
            }
        }
    };
    public Runnable mCheckRunnable = new Runnable() {
        public void run() {
            Log.i(TAG, "=== checkOTGdevices! ===");
            checkOTGdevices();
            if (mounted[0] != 0) {
                mTextView.setText(getResources().getText(R.string.otg_no_devices));
                mHandler.postDelayed(mCheckRunnable, 1000);
            } else {
                startVtThread();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mTextView.setText(getResources().getText(R.string.otg_begin_test));
        mounted[0] = 1;
        result[0] = 1;
        checkOTGdevices();
        if (mounted[0] != 0) {
            mTextView.setText(getResources().getText(R.string.otg_no_devices));
            mHandler.postDelayed(mCheckRunnable, 1000);
        } else {
            startVtThread();
        }
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mCheckRunnable);
        mHandler.removeMessages(UPDATE_TIME);
        super.onPause();
    }

    private void startVtThread() {
        Log.i(TAG,
                "=== create thread to execute OTG test command! ===");
        /* SPRD Bug 766333: OTG test should use mouse not USB. @{ */
        if (Const.isBoardISharkL210c10()) {
            result[0] = 0;
            mHandler.post(mRunnable);
        } else {
            Thread vtThread = new Thread() {
                public void run() {
                    FileInputStream in = null;
                    FileOutputStream out = null;
                    try {
                        if (mounted[0] == 0) {
                            File fp = new File(usbMassStoragePath, SPRD_OTG_TESTFILE);
                            if (fp.exists())
                                fp.delete();
                            fp.createNewFile();
                            out = new FileOutputStream(fp);
                            mOTGTestFlag[0] = '7';
                            out.write(mOTGTestFlag, 0, 1);
                            out.close();
                            in = new FileInputStream(fp);
                            in.read(mOTGTestFlag, 0, 1);
                            in.close();
                            if (mOTGTestFlag[0] == '7') {
                                result[0] = 0;
                            } else {
                                result[0] = 1;
                            }
                        }
                        //mHandler.post(mRunnable);
                        mHandler.postDelayed(mRunnable, 2000);
                    } catch (IOException e) {
                        Log.i(TAG, "=== error: Exception happens when OTG I/O! ===");
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                                out = null;
                            }
                            if (in != null) {
                                in.close();
                                in = null;
                            }
                        } catch (IOException io) {
                            Log.e(TAG, "close in/out err");
                        }
                    }
                }
            };
            vtThread.start();
        }
        /* @} */
    }
}
