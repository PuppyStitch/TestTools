package com.sprd.validationtools.itemstest.sptest;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.dspread.xpos.CQPOSService;
import com.dspread.xpos.QPOSService;
import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

import java.util.Hashtable;


public class ICCardTestActivity extends BaseActivity {

    private static final String TAG = "ICCardTestActivity";

    QPOSService qposService;
    Context mContext;

    private Button mStartButton;
    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 8000;
    private boolean isOk = false;
    int count = 0;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(ICCardTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ICCardTestActivity.this, R.string.text_fail,
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
        setContentView(R.layout.activity_nfc_test_layout);
        mStartButton = findViewById(R.id.btn_start);
        mStartButton.setText(getResources().getText(R.string.color_temperature_start));
        mStartButton.setOnClickListener(view -> start());
        mContext = this;
        disablePassButton();
    }

    private void start() {
        qposService.testPosFunctionCommand(8, QPOSService.TestCommand.ICC_TEST);
        mStartButton.setEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        qposService.closeUart();
        Log.i(TAG, "onDestroy closeUart");
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        qposService.setDeviceAddress("/dev/ttyS1");
        qposService.openUart();
    }

    class MyPosListener extends CQPOSService {
        @Override
        public void onQposTestCommandResult(boolean isSuccess, String data) {
            super.onQposTestCommandResult(isSuccess, data);
            Log.i(TAG, "isSuccess " + isSuccess + " " + data);
            isOk = isSuccess;
            if (count < 3 && !isSuccess) {
                qposService.testPosFunctionCommand(8, QPOSService.TestCommand.ICC_TEST);
                count++;
                return;
            }
//            mStartButton.setEnabled(true);
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
