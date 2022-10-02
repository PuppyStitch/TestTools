package com.sprd.validationtools.itemstest.sptest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dspread.xpos.CQPOSService;
import com.dspread.xpos.QPOSService;
import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

import java.util.Hashtable;
import java.util.Map;

public class SPVersionTestActivity extends BaseActivity {

    private static final String TAG = "SPVersionTestActivity";

    QPOSService qposService;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sp_version_layout);
        textView = findViewById(R.id.sp_version);
        open(QPOSService.CommunicationMode.UART);
        qposService.getQposInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qposService.closeUart();
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
        public void onError(QPOSService.Error errorState) {
            super.onError(errorState);
            Log.i(TAG,"Error " + errorState.name());
            storeRusult(false);
        }

        @Override
        public void onQposInfoResult(Hashtable<String, String> posInfoData) {
            super.onQposInfoResult(posInfoData);
            textView.setText(posInfoData.get("firmwareVersion"));
            for (Map.Entry<String, String> entry : posInfoData.entrySet()) {
                Log.i(TAG, entry.getKey() + " = " + entry.getValue());
            }
        }
    }
}
