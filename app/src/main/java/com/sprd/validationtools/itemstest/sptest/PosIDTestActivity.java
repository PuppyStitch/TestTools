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

import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class PosIDTestActivity extends BaseActivity {

    private static final String TAG = "PosIDTestActivity";

    TextView posIdTV;

    QPOSService qposService;

    String filePath = "/sys/devices/platform/soc/soc:ap-apb/70800000.i2c/i2c-3/3-0048/cts_firmware/driver_builtin_firmware";

    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 16000;
    private boolean isOk = false;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isOk) {
                Toast.makeText(PosIDTestActivity.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PosIDTestActivity.this, R.string.text_fail,
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
        setContentView(R.layout.pos_id_test_layout);
        posIdTV = findViewById(R.id.pos_id);
//        open(QPOSService.CommunicationMode.UART);
//        qposService.getQposId(3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(runnable, TIMEOUT);
        String content = readFile(filePath);
        Log.i(TAG, "the content is = " + content);
//        tempPre = str.charAt(str.indexOf("Current temperature:") + "Current temperature:".length());
        if (content != null) {
            int index = content.indexOf("hwid: ");
            int length = "hwid: ".length();
            String id = content.substring(index + length, index + 6 + length);
            posIdTV.setText(id);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
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
            posIdTV.setText(posId.get("posId"));
            isOk = true;
            for (Map.Entry<String, String> entry : posId.entrySet()) {
                Log.i(TAG, entry.getKey() + " = " + entry.getValue());
            }
        }
    }

    private String readFile(String path) {
        char[] buffer = new char[1024];
        String batteryElectronic = "";
        FileReader file = null;
        try {
            file = new FileReader(path);
            int len = file.read(buffer, 0, 1024);
            batteryElectronic = new String(buffer, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (file != null) {
                    file.close();
                    file = null;
                }
            } catch (IOException io) {
                Log.w(TAG, "read file close fail");
            }
        }
        isOk = true;
        return batteryElectronic;
    }
}
