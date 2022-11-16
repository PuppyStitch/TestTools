package com.sprd.validationtools.itemstest.sptest;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.dspread.xpos.CQPOSService;
import com.dspread.xpos.QPOSService;
import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import java.util.Hashtable;


public class MyNFCTestActivity extends BaseActivity {

    QPOSService qposService;

    private static final String TAG = "MyNFCTestActivity";

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
                Toast.makeText(MyNFCTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyNFCTestActivity.this, R.string.text_fail,
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
        setContentView(R.layout.activity_nfc_test_layout);
        count = 0;
        mStartButton = findViewById(R.id.btn_start);
        mStartButton.setOnClickListener(view -> start());
        disablePassButton();
        open(QPOSService.CommunicationMode.UART);
        mContext = this;
        qposService.resetPosStatus();
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

    private void start() {
//        qposService.testPosFunctionCommand(8, QPOSService.TestCommand.NFC_TEST);
        qposService.printText(10, "test");
        mStartButton.setEnabled(false);
    }

    private void open(QPOSService.CommunicationMode mode) {
        MyPosListener listener = new MyPosListener();
        //实现类的单例模式
        qposService = QPOSService.getInstance(mode);
        if (qposService == null) {
            return;
        }
        if (mode == QPOSService.CommunicationMode.USB_OTG_CDC_ACM) {
            qposService.setUsbSerialDriver(QPOSService.UsbOTGDriver.CH34XU);
        }
        qposService.setD20Trade(true); //跟祥承同步，SDK默认打开D20开关
        qposService.setConext(this);
        //通过handler处理，监听MyPosListener，实现QposService的接口，（回调接口）
        Handler handler = new Handler(Looper.myLooper());
        qposService.initListener(handler, listener);
    }

    class MyPosListener extends CQPOSService {
        @Override
        public void onQposTestCommandResult(boolean isSuccess, String data) {
            super.onQposTestCommandResult(isSuccess, data);
            isOk = isSuccess;
            if (count < 3 && !isSuccess) {
                qposService.testPosFunctionCommand(8, QPOSService.TestCommand.NFC_TEST);
                count++;
                return;
            }
            mHandler.post(runnable);
        }

        @Override
        public void onQposTestResult(Hashtable<String, String> testResultData) {
            super.onQposTestResult(testResultData);
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
