package com.sprd.validationtools.itemstest.sensor;

/** BUG479359 zhijie.yang 2016/5/5 MMI add the magnetic sensors and the prox sensor calibration**/
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;
import com.simcom.testtools.R;
import com.sprd.validationtools.utils.FileUtils;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.util.Log;
import android.os.Handler;
import android.os.Message;

public class MSensorCalibrationActivity extends BaseActivity {

    private static final String TAG = "MSensorCalibrationActivity";
    private static final int SET_CMD_COMPLETE = 1;
    private static final int CALIBRATION_SUCCESS = 2;
    private static final int CALIBRATION_FAIL = 3;

    private static final String SET_CMD = "0 2 1"; // start calibrating
    private static final String GET_RESULT = "1 2 1";// get result of Calibration
    private static final String SAVE_RESULT = "3 2 1";// save the result

    private static final String PASS_NUMBER = "0";
    private static final String TEST_OK = "2";
    private TextView mTipsText;
    private boolean isOk = false;
    private boolean saveResult = false;
    private Context mContext;

    private SensorUtils mSensorUtils = null;

    private Runnable mR = new Runnable() {
        public void run() {
            mTipsText.setText(mContext.getResources().getString(
                    R.string.m_sensor_calibration_fail));
            /*SPRD bug 760913:Test can pass/fail must click button*/
            if(Const.isBoardISharkL210c10()){
                Log.d("", "isBoardISharkL210c10 is return!");
                return;
            }
            /*@}*/
            /*SPRD bug 742118:Add test fail while sensor calibration fail*/
            storeRusult(false);
            finish();
            /*@}*/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_m_calibration);
        mContext = this;
        mTipsText = (TextView) findViewById(R.id.title_sensor_m);
        /*SPRD bug 760913:Test can pass/fail must click button*/
        if(Const.isBoardISharkL210c10()){
            mPassButton.setVisibility(View.GONE);
        }
        /*@}*/
        mSensorUtils = new SensorUtils(this, Sensor.TYPE_MAGNETIC_FIELD);
        mSensorUtils.enableSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSensorCalibration();
    }

    private void startSensorCalibration() {
        new SensorCalibrationThread().start();
    }

    class SensorCalibrationThread extends Thread {
        public void run() {
            sensorCalibration();
        };
    };

    /**
     ** start calibrating echo "0 [SENSOR_ID] 1" > calibrator_cmd
     **/
    private void sensorCalibration() {
    	FileUtils.writeFile(Const.CALIBRATOR_CMD, SET_CMD);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mHandler.sendMessage(mHandler.obtainMessage(SET_CMD_COMPLETE));

    }

    /**
     * start calibrating echo "2 [SENSOR_ID] 1" > calibrator_cmd cat calibrator_data, if the get
     * value is 2 ,the test is ok ,or test is fial
     **/
    private void getResult() {
        boolean isOK = false;
        FileUtils.writeFile(Const.CALIBRATOR_CMD, GET_RESULT);
        String getResult = FileUtils.readFile(Const.CALIBRATOR_DATA);
        saveResult = saveResult();
        Log.d(TAG, "the result of boolen saveResult: " + saveResult);
        Log.d(TAG, "the result of MSensor calibration: " + getResult);
        if (saveResult && getResult != null && TEST_OK.equals(getResult.trim())) {
            isOK = true;
        }
        if (isOK) {
            mHandler.sendMessage(mHandler.obtainMessage(CALIBRATION_SUCCESS));
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(CALIBRATION_FAIL));
        }
    }

    /**
     * save the result echo "3 [SENSOR_ID] 1" > calibrator_cmd cat calibrator_data to save test
     * result
     **/
    private boolean saveResult() {
        Log.d(TAG, "saveResult...");
        FileUtils.writeFile(Const.CALIBRATOR_CMD, SAVE_RESULT);
        String saveResult = FileUtils.readFile(Const.CALIBRATOR_DATA);
        Log.d(TAG, "save result: " + saveResult);
        if (saveResult != null && PASS_NUMBER.equals(saveResult.trim())) {
            isOk = true;
            Log.d(TAG, "save result isOk: " + isOk);
        }
        return isOk;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_CMD_COMPLETE:
                    new Thread(new Runnable() {
                        public void run() {
                            getResult();
                        }
                    }).start();
                    break;
                case CALIBRATION_SUCCESS:
                    Toast.makeText(mContext, R.string.text_pass,
                            Toast.LENGTH_SHORT).show();
                    /*SPRD bug 760913:Test can pass/fail must click button*/
                    if(Const.isBoardISharkL210c10()){
                        Log.d("", "isBoardISharkL210c10 is return!");
                        mPassButton.setVisibility(View.VISIBLE);
                        return;
                    }
                    /*@}*/
                    storeRusult(true);
                    finish();
                    break;
                case CALIBRATION_FAIL:
                    mHandler.post(mR);
                    break;
                default:
            }
        }

    };

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mR);
        if(mSensorUtils != null){
            mSensorUtils.disableSensor();
        }
        super.onDestroy();
    }
}
