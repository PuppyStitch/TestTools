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
import java.util.Map;

public class PosIDTestActivity extends BaseActivity {

    private static final String TAG = "PosIDTestActivity";

    QPOSService qposService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        open(QPOSService.CommunicationMode.UART);
        qposService.getQposId(3000);

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
        public void onError(QPOSService.Error errorState) {
            super.onError(errorState);
            Log.i(TAG,"Error " + errorState.name());
            storeRusult(false);
        }

        @Override
        public void onQposIdResult(Hashtable<String, String> posId) {
            super.onQposIdResult(posId);
            int a = 0;
            Log.i(TAG, "onQposIdResult");
            Log.i(TAG, posId.get("posId"));

            for (Map.Entry<String, String> entry : posId.entrySet()) {
                Log.i(TAG, entry.getKey() + " = " + entry.getValue());
            }
        }
    }
}
