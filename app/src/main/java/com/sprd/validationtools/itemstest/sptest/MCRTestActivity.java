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

    private Button mStartButton;
    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 16000;
    private boolean isOk = false;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(MCRTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(MCRTestActivity.this, R.string.text_fail,
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
        open(QPOSService.CommunicationMode.UART);
        LinearLayout barcodeLayout = new LinearLayout(this);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        barcodeLayout.setLayoutParams(params);
        barcodeLayout.setOrientation(LinearLayout.VERTICAL);
        barcodeLayout.setGravity(Gravity.CENTER);
        mStartButton = new Button(this);
        mStartButton.setTextSize(35);
        barcodeLayout.addView(mStartButton);
        setContentView(barcodeLayout);
        setTitle("MyNFCTestActivity");
        mStartButton.setText(getResources().getText(R.string.color_temperature_start));
        mStartButton.setOnClickListener(view -> start());
        mContext = this;
    }

    private void start() {
        qposService.testPosFunctionCommand(3000, QPOSService.TestCommand.MCR_TEST);
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
            Log.i(TAG, "isSuccess " + isSuccess);
            if (isSuccess && "010101".equals(data)) {
                Toast.makeText(MCRTestActivity.this, mContext.getText(R.string.text_pass) +
                        " " + data, Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(MCRTestActivity.this, mContext.getText(R.string.text_fail) +
                        " " + data, Toast.LENGTH_SHORT).show();
                storeRusult(false);
            }
            finish();
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
