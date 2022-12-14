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

public class MCRTestActivity extends BaseActivity {

    private static final String TAG = "MCRTestActivity";

    QPOSService qposService;
    Context mContext;

    int count = 0;
    private Button mStartButton;
    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 16000;
    private boolean isOk = false;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(MCRTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MCRTestActivity.this, R.string.text_fail,
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
        count = 0;
        open(QPOSService.CommunicationMode.UART);
        setContentView(R.layout.activity_mcr_test_layout);
        mStartButton = findViewById(R.id.btn_start);
        mStartButton.setOnClickListener(view -> start());
        mContext = this;
        disablePassButton();
    }

    private void start() {
        qposService.testPosFunctionCommand(8, QPOSService.TestCommand.MCR_TEST);
        mStartButton.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(runnable, TIMEOUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
        qposService.closeUart();
        qposService.resetPosStatus();
    }

    private void open(QPOSService.CommunicationMode mode) {
        MyPosListener listener = new MyPosListener();
        //????????????????????????
        qposService = QPOSService.getInstance(mode);
        if (qposService == null) {
            return;
        }
        if (mode == QPOSService.CommunicationMode.USB_OTG_CDC_ACM) {
            qposService.setUsbSerialDriver(QPOSService.UsbOTGDriver.CH34XU);
        }
        qposService.setD20Trade(true); //??????????????????SDK????????????D20??????
        qposService.setConext(this);
        //??????handler???????????????MyPosListener?????????QposService??????????????????????????????
        Handler handler = new Handler(Looper.myLooper());
        qposService.initListener(handler, listener);
        qposService.setDeviceAddress("/dev/ttyS1");
        qposService.openUart();
    }

    class MyPosListener extends CQPOSService {
        @Override
        public void onQposTestCommandResult(boolean isSuccess, String data) {
            super.onQposTestCommandResult(isSuccess, data);
            Log.i(TAG, "isSuccess " + isSuccess);
            if (isSuccess && "010101".equals(data)) {
                isOk = true;
            }

            if (count < 3 && !isSuccess) {
                qposService.testPosFunctionCommand(8, QPOSService.TestCommand.MCR_TEST);
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
