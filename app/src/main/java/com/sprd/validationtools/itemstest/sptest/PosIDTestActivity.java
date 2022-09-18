package com.sprd.validationtools.itemstest.sptest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dspread.xpos.CQPOSService;
import com.dspread.xpos.QPOSService;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;

import java.util.Hashtable;

public class PosIDTestActivity extends BaseActivity {

    private static final String TAG = "PosIDTestActivity";

    QPOSService qposService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        open(QPOSService.CommunicationMode.UART);
        qposService.getQposId(1000);
    }

    private void open(QPOSService.CommunicationMode mode) {
        MyPosListener listener = new MyPosListener();
        //实现类的单例模式
        qposService = QPOSService.getInstance(mode);
        if (qposService == null) {
//            Toast.makeText(this,"pos == null",0).show();
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
            Log.i(TAG,"isSuccess "+ isSuccess);
            setResult(isSuccess ? Const.SUCCESS : Const.FAIL);
            finish();
        }

        @Override
        public void onQposTestResult(Hashtable<String, String> testResultData) {
            super.onQposTestResult(testResultData);

        }

        @Override
        public void onError(QPOSService.Error errorState) {
            super.onError(errorState);
            Log.i(TAG,"Error "+errorState.name());
        }

        @Override
        public void onQposIdResult(Hashtable<String, String> posId) {
            super.onQposIdResult(posId);
            int a = 0;
            Log.i(TAG, "onQposIdResult");
            Log.i(TAG, posId.get(0));
        }
    }
}
