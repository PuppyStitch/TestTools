package com.sprd.validationtools.itemstest.sptest;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.dspread.xpos.CQPOSService;
import com.dspread.xpos.QPOSService;
import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.utils.RootCmdUtils;

import java.util.Hashtable;


public class PrintTestActivity2 extends BaseActivity {

    QPOSService qposService;
    private static final String TAG = "PrintTestActivity2";

    private DevicePolicyManager policyManager;
    private ComponentName adminReceiver;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    String str = "this is for testing this is for testing this is for testing this is for testing " +
            "this is for testing this is for testing this is for testing this is for testing " +
            "this is for testing this is for testing this is for testing this is for testing ";

    Button mInitButton, mTestButton;

    int count = 0;
    private Context mContext;
    private Button mStartButton;
    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 16000;
    private boolean isOk = false;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                enablePassButton();
                Toast.makeText(PrintTestActivity2.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PrintTestActivity2.this, R.string.text_fail,
                        Toast.LENGTH_SHORT).show();
            }
            storeRusult(isOk);
            mHandler.removeCallbacks(runnable);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_test_layout);
        count = 0;
        mStartButton = findViewById(R.id.btn_start);
        mStartButton.setOnClickListener(v -> start());

        mInitButton = findViewById(R.id.btn_init);
        mTestButton = findViewById(R.id.btn_test);

        mInitButton.setOnClickListener(v -> checkAndTurnOnDeviceManager());

        mTestButton.setOnClickListener(v -> checkScreenOffAndDelayOn());

        policyManager = (DevicePolicyManager) PrintTestActivity2.this.getSystemService(Context.DEVICE_POLICY_SERVICE);

        disablePassButton();
        Log.d(TAG, "oncreate 111");
        open(QPOSService.CommunicationMode.UART);
//        blueTootchAddress = "/dev/ttys1";
        Log.d(TAG, "oncreate 222");
//        qposService.setDeviceAddress(blueTootchAddress);
        qposService.openUart();
        mContext = this;
        Log.d(TAG, "oncreate 333");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
        qposService.closeUart();
        qposService.resetPosStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(runnable, TIMEOUT);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@SuppressLint("HandlerLeak") Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    checkScreenOn(null);
                    break;
                case 2:

                    break;
            }
        }
    };

    @SuppressLint("InvalidWakeLockTag")
    public void checkScreenOn(View view) {
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        mWakeLock.release();
    }

    public void checkScreenOffAndDelayOn() {
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
            handler.sendEmptyMessageDelayed(1, 5000);
        } else {
            Log.i(TAG, "没有设备管理权限");
        }
    }

    public void checkAndTurnOnDeviceManager() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启后就可以使用锁屏功能了...");//显示位置见图二
        startActivityForResult(intent, 0);
    }

    private void start() {
        Log.d(TAG, "start（） 111");
        qposService.printText(10, str);
        Log.d(TAG, "start（） 222");
        mStartButton.setEnabled(false);
    }

    private void open(QPOSService.CommunicationMode mode) {
        MyPosListener listener = new MyPosListener();
        //实现类的单例模式
        qposService = QPOSService.getInstance(mode);
        if (qposService == null) {
            return;
        }
        /*
        if (mode == QPOSService.CommunicationMode.USB_OTG_CDC_ACM) {
            qposService.setUsbSerialDriver(QPOSService.UsbOTGDriver.CH34XU);
        }
         */
        qposService.setD20Trade(true); //跟祥承同步，SDK默认打开D20开关
        qposService.setConext(this);
        //通过handler处理，监听MyPosListener，实现QposService的接口，（回调接口）
        Handler handler = new Handler(Looper.myLooper());
        qposService.initListener(handler, listener);
    }

    class MyPosListener extends CQPOSService {
        @Override
        public void onQposPrintOperateResult(final Hashtable<String, String> printOperateResult) {
            super.onQposPrintOperateResult(printOperateResult);
            Log.d(TAG, "onQposTestResult（） onQposPrintOperateResult");
            String code = printOperateResult.get("code");
            Log.d(TAG, "code " + code);
//            mHandler.post(runnable);
        }

        @Override
        public void onQposTestResult(Hashtable<String, String> testResultData) {
            super.onQposTestResult(testResultData);
            Log.d(TAG, "onQposTestResult（） 111");
        }

        @Override
        public void onError(QPOSService.Error errorState) {
            super.onError(errorState);
            Log.i(TAG, "Error " + errorState.name());
            isOk = false;
            storeRusult(false);
        }
    }
}
