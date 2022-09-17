
package com.sprd.validationtools.itemstest.charger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.os.SystemProperties;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableRow;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;
import com.sprd.validationtools.PhaseCheckParse;
import com.simcom.testtools.R;

public class ChargerTest extends BaseActivity {

    private static final String TAG = "ChargerTest";
    private static final String INPUT_ELECTRONIC = "/sys/class/power_supply/battery/real_time_current";
    private static final String CHARGER_ELECTRONIC = "/sys/class/power_supply/sprdfgu/fgu_current";
    private static final String ENG_CHARGER_VOL = "/sys/class/power_supply/battery/charger_voltage";
    private static final String ENG_BATTERY_TVOL = "/sys/class/power_supply/battery/real_time_voltage";
    /* SPRD Bug 773805:Show NTC temperature in charger test. @{ */
    private static final String ENG_CHARGER_TEMP = "sys/class/power_supply/battery/temp";
    /* @} */
    /*SPRD bug 850012:Read battery id*/
    private String mBatteryTypeId = "";
    private TableRow mBatteryTypeIdTableRow = null;
    private TextView mBatteryTypeIdTextView= null;
    private static final String ID_BYD = "04";
    private static final String ID_BAK = "17";
    private static final String FD_BATTERY_ID = "sys/class/power_supply/battery/bat_id";
    /*@}*/

    private static final String STATUS = "status";
    private static final String PLUGGED = "plugged";
    private static final String VOLTAGE = "voltage";
    private static final String EXT_CHARGE_IC = "ext charge ic";
    private static final String TEST_RESULT_SUCCESS = "success";
    private static final String TEST_RESULT_FAIL = "fail";

    private TextView statusTextView, pluggedTextView, voltageTextView,
            mElectronicTextView, mBatteryTextView, mTestResultTextView,
            mTemperatureTextView, mBatteryElectronicTextView;
    private String mPluggeString = null;
    private boolean mIsPlugUSB = false;
    private float mChargerElectronic;
    private float mChargerVoltage;
    /* SPRD Bug 773805:Show NTC temperature in charger test. @{ */
    private float mBatteryTemperature;
    private TableRow mTemperatureTableRow;
    private TableRow mBatteryElectronicTableRow;

    /** Replace real_time_current*/
    private static final String ENG_CHARGER_CURRENT_K414 = "/sys/class/power_supply/battery/current_now";
    /** Replace fgu_current*/
    private static final String ENG_CHARGER_FGU_CURRENT_K414 = "/sys/class/power_supply/sc27xx-fgu/current_now";
    /** Replace charger_voltage*/
    private static final String ENG_CHARGER_VOLTAGE_K414 = "/sys/class/power_supply/sc27xx-fgu/constant_charge_voltage";
    private static final String ENG_REAL_BATTERY_TEMPERATURE = "sys/class/power_supply/sc27xx-fgu/temp";

    private boolean mIsSupportK414 = false;
    private static final boolean ENABLE_BATTRY_TEMPERATURE = com.sprd.validationtools.utils.FileUtils.fileIsExists(ENG_REAL_BATTERY_TEMPERATURE);

    private void initSupportK414(){
        File file = new File(ENG_CHARGER_VOLTAGE_K414);
        Log.d(TAG, "initSupportK414 file="+file + ",exists="+file.exists());
        if(file != null && file.exists()){
            mIsSupportK414 = true;
        }else{
            mIsSupportK414 = false;
        }
    }

    private boolean isSupportK414(){
        Log.d(TAG, "isSupportK414 mIsSupportK414="+mIsSupportK414);
        return mIsSupportK414;
    }

    private Runnable mRealtimeShow = new Runnable() {
        public void run() {
            mHandler.removeCallbacks(this);
            initView();
            mHandler.postDelayed(this, 1000);
        }
    };
    /* @} */

    private String mInputCurrent = null;
    private int mRetryNum = 0;
    private int mWaitTime = 3000;

    private Handler mHandler;

    private Runnable mElectronicUpdate = new Runnable() {
        public void run() {

            if(Const.isBoardISharkL210c10()){
                mBatteryTypeId = readFile(FD_BATTERY_ID).trim();
                Log.d(TAG, "mBatteryTypeId="+mBatteryTypeId);
                if(mBatteryTypeIdTextView != null){
                    if(!TextUtils.isEmpty(mBatteryTypeId)){
                        String text = getString(R.string.battery_type_id);
                        Log.d(TAG, "text ="+ text);
                        mBatteryTypeIdTextView.setText(text + (mBatteryTypeId.equals("0") ? ID_BYD : ID_BAK));
                    }
                }
            }
            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>(){

                @Override
                protected String doInBackground(Void... params) {
                    String testResult = getInputElectronicNewStep();
                    return testResult;
                }
                protected void onPostExecute(String result) {
                    String testResult = result;
                    Log.d(TAG, "mElectronicUpdate data,mChargerVoltage=" + mChargerVoltage
                            + ", mChargerElectronic=" + mChargerElectronic
                            + ", mBatteryTemperature=" + mBatteryTemperature
                            + ",testResult:" + testResult);

                    if (TEST_RESULT_SUCCESS.equals(testResult)) {

                        if (mIsPlugUSB) {
                            mTestResultTextView.setText(getString(R.string.charger_test_success));
                            mTestResultTextView.setTextColor(Color.GREEN);
                            /*SPRD bug 760913:Test can pass/fail must click button*/
                            if(Const.isBoardISharkL210c10()){
                                mPassButton.setVisibility(View.VISIBLE);
                                return;
                            }
                            /*@}*/
                            storeRusult(true);
                            mHandler.postDelayed(mCompleteTest, 2000);
                        } else {
                            mHandler.postDelayed(mElectronicUpdate, 2000);
                        }

                    } else {

                        if (mIsPlugUSB) {
                            mRetryNum++;
                            if (mRetryNum <= 5) {
                                mWaitTime = 500 * mRetryNum;

                                mHandler.post(mElectronicUpdate);
                                Log.d(TAG, "retry test num:" + mRetryNum + ",wait time is " + mWaitTime);
                            } else {
                                mTestResultTextView.setText(getString(R.string.charger_test_fail));
                                mTestResultTextView.setTextColor(Color.RED);
                                storeRusult(false);
                                mHandler.postDelayed(mCompleteTest, 2000);
                            }
                        } else {
                            mHandler.postDelayed(mElectronicUpdate, 2000);
                        }
                    }
                };
            };
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    };

    private Runnable mCompleteTest = new Runnable() {
        public void run() {
            /*SPRD bug 760913:Test can pass/fail must click button*/
            if(Const.isBoardISharkL210c10()){
                Log.d("", "isBoardISharkL210c10 is return!");
                return;
            }
            /*@}*/
            mHandler.postDelayed(new Runnable() {
                 public void run() {
                    finish();
                }
            }, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle(R.string.battery_title_text);
        setContentView(R.layout.battery_charged_result);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        mHandler = new Handler();
        //Update kernel 4.14
        initSupportK414();
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        pluggedTextView = (TextView) findViewById(R.id.pluggedTextView);
        voltageTextView = (TextView) findViewById(R.id.voltageTextView);
        mElectronicTextView = (TextView) findViewById(R.id.electronicTextView);
        mBatteryTextView = (TextView) findViewById(R.id.batteryelectronicTextView);
        mTestResultTextView = (TextView) findViewById(R.id.test_resultTextView);
        /* SPRD Bug 773805:Show NTC temperature in charger test. @{ */
        mTemperatureTextView = (TextView) findViewById(R.id.batterytemperatureTextView);
        mTemperatureTableRow = (TableRow) findViewById(R.id.TableRow06);
        if(Const.isBoardISharkL210c10() || ENABLE_BATTRY_TEMPERATURE){
            mTemperatureTableRow.setVisibility(View.VISIBLE);
        }
        /* @} */
        /*SPRD bug 850012:Read battery id*/
        mBatteryTypeIdTextView = (TextView) findViewById(R.id.battery_type_id);
        mBatteryTypeIdTableRow = (TableRow) findViewById(R.id.TableRow08);
        if(Const.isBoardISharkL210c10()){
            mBatteryTypeIdTableRow.setVisibility(View.VISIBLE);
        }
        /*@}*/

        mBatteryElectronicTextView = (TextView) findViewById(R.id.battery_electronic_tv);
        mBatteryElectronicTableRow = (TableRow) findViewById(R.id.TableRow09);
        if (ENABLE_BATTRY_TEMPERATURE) {
            mTemperatureTableRow.setVisibility(View.VISIBLE);
            mBatteryElectronicTableRow.setVisibility(View.VISIBLE);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
        /*SPRD bug 760913:Test can pass/fail must click button*/
        if(Const.isBoardISharkL210c10()){
            mPassButton.setVisibility(View.GONE);
        }
        /*@}*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ENABLE_BATTRY_TEMPERATURE) {
            int batteryElectronic = Integer.parseInt(readFile(ENG_CHARGER_FGU_CURRENT_K414).trim()) / 1000;
            mBatteryElectronicTextView.setText(batteryElectronic + " ma");

            mBatteryTemperature = getDateFromNode(ENG_REAL_BATTERY_TEMPERATURE);
            mTemperatureTextView.setText(mBatteryTemperature / 10 + " \u2103");
        }
        mHandler.postDelayed(mElectronicUpdate, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mElectronicUpdate);
        mHandler.removeCallbacks(mRealtimeShow);
    }

    private String getInputElectronicNewStep() {
        String result = "";
        String inputCurrent = "";
        Log.d(TAG, "getInputElectronicNewStep inputCurrent[" + inputCurrent + "]");
        try {
            if (Const.isBoardISharkL210c10()) {
                mHandler.postDelayed(mRealtimeShow, 100);
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initView();
                    }
                });
            }
            //step1.Stop charge, read  current
            stopCharge();
            Thread.sleep(2000);
            int c1 = 0;
            if(isSupportK414()){
                c1 = Integer.parseInt(readFile(ENG_CHARGER_FGU_CURRENT_K414).trim()) / 1000;
            }else{
                c1 = Integer.parseInt(readFile(CHARGER_ELECTRONIC).trim());
            }
            Log.d(TAG, "getInputElectronicNewStep inputCurrent c1=[" + c1 + "]");
            final int c11 = c1;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBatteryTextView.setText(c11 + " ma");
            if (ENABLE_BATTRY_TEMPERATURE) {
                int batteryElectronic = Integer.parseInt(readFile(ENG_CHARGER_FGU_CURRENT_K414).trim()) / 1000;
                mBatteryElectronicTextView.setText(batteryElectronic + " ma");
            }
                }
            });
            //step2.Start charge, read charging current
            startCharge();
            Thread.sleep(2000);
            int c2 = 0;
            if(isSupportK414()){
                c2 = Integer.parseInt(readFile(ENG_CHARGER_FGU_CURRENT_K414).trim())  / 1000;
            }else{
                c2 = Integer.parseInt(readFile(CHARGER_ELECTRONIC).trim());
            }
            Log.d(TAG, "getInputElectronicNewStep inputCurrent c2=[" + c2 + "]");
            int i1 = c2 - c1;
            Log.d(TAG, "getInputElectronicNewStep inputCurrent i1=[" + i1 + "]");
            Log.d(TAG, "getInputElectronicNewStep inputCurrent mChargerVoltage=[" + mChargerVoltage + "]");
            //i1 >= 200mA PASS
            if (i1 >= 200) {
                result = TEST_RESULT_SUCCESS;
            }
            //i1 > 100mA && i1 < 200mA && mChargerVoltage >= 4100 PASS
            else if(i1 > 100 && i1 < 200 && mChargerVoltage >= 4100){
                result = TEST_RESULT_SUCCESS;
            }
            else {
                result = TEST_RESULT_FAIL;
            }
            final int i11 = i1;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBatteryTextView.setText(i11 + " ma");
            if (ENABLE_BATTRY_TEMPERATURE) {
                int batteryElectronic = Integer.parseInt(readFile(ENG_CHARGER_FGU_CURRENT_K414).trim()) / 1000;
                mBatteryElectronicTextView.setText(batteryElectronic + " ma");
            }
                }
            });
        } catch (NumberFormatException | InterruptedException  e) {
            Log.w(TAG, "getInputElectronicNewStep fail", e);
            e.printStackTrace();
        }
        return result;
    }

    private void initView() {
        //Support kernel 4.14
        if(isSupportK414()){
            mChargerElectronic = getDateFromNode(ENG_CHARGER_CURRENT_K414) / 1000;
            mChargerVoltage = getDateFromNode(ENG_CHARGER_VOLTAGE_K414) / 1000;
        }else{
            mChargerElectronic = getDateFromNode(CHARGER_ELECTRONIC);
            mChargerVoltage = getDateFromNode(ENG_CHARGER_VOL);
        }
        Log.d(TAG, "initView mChargerElectronic="+mChargerElectronic+",mChargerVoltage="+mChargerVoltage);
        /* SPRD Bug 773805:Show NTC temperature in charger test. @{ */
        if (ENABLE_BATTRY_TEMPERATURE) {
            mBatteryTemperature = getDateFromNode(ENG_REAL_BATTERY_TEMPERATURE);
        } else {
            mBatteryTemperature = getDateFromNode(ENG_CHARGER_TEMP);
        }
        /* @} */

        if (mChargerElectronic > -40.0 && mIsPlugUSB) {
            mBatteryTextView.setText(mChargerElectronic + " ma");
        } else {
            mBatteryTextView.setText("n/a");
        }

        // General power of the test will have an initial value of 40mv.
        // Unfriendly so set a value greater than 100 and must plug usb or
        // ac
        if (mChargerVoltage >= 100.0 && mIsPlugUSB) {
            mElectronicTextView.setText(mChargerVoltage + " mv");
        } else {
            mElectronicTextView.setText("n/a");
        }

        /* SPRD Bug 773805:Show NTC temperature in charger test. @{ */
        if(Const.isBoardISharkL210c10() || ENABLE_BATTRY_TEMPERATURE){
            mTemperatureTextView.setText(mBatteryTemperature / 10 + " \u2103");
        }
        /* @} */
    }

    public boolean isNum(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    private float getDateFromNode(String nodeString) {
        char[] buffer = new char[1024];
        // Set a special value -100, to distinguish mChargerElectronic greater
        // than -40.
        float batteryElectronic = -100;
        FileReader file = null;
        try {
            file = new FileReader(nodeString);
            int len = file.read(buffer, 0, 1024);
            batteryElectronic = Float.valueOf((new String(buffer, 0, len)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }finally{
            try {
                if (file != null) {
                    file.close();
                    file = null;
                }
            } catch (IOException io) {
                Log.e(TAG, "getDateFromNode fail , nodeString is:" + nodeString);
            }
        }
        return batteryElectronic;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra(STATUS, 0);
                int plugged = intent.getIntExtra(PLUGGED, 0);
                int voltage = intent.getIntExtra(VOLTAGE, 0);
                String statusString = "";
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        statusString = getResources().getString(R.string.charger_unknown);
                        break;
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        statusString = getResources().getString(R.string.charger_charging);
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        statusString = getResources().getString(R.string.charger_discharging);
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        statusString = getResources().getString(R.string.charger_not_charging);
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        statusString = getResources().getString(R.string.charger_full);
                        break;
                    default:
                        break;
                }
                switch (plugged) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        mIsPlugUSB = true;
                        mPluggeString = getResources().getString(R.string.charger_ac_plugged);
                        mTestResultTextView.setVisibility(View.VISIBLE);
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        mIsPlugUSB = true;
                        mPluggeString = getResources().getString(R.string.charger_usb_plugged);
                        mTestResultTextView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        mIsPlugUSB = false;
                        mPluggeString = getResources().getString(R.string.charger_no_plugged);
                        mTestResultTextView.setText(getString(R.string.charging_test));
                        mTestResultTextView.setTextColor(Color.WHITE);
                        mTestResultTextView.setVisibility(View.GONE);
                        // Prevent unplug the usb cable is still charging
                        // status.
                        if (statusString.equals(getString(R.string.charger_charging))) {
                            statusString = getResources().getString(R.string.charger_discharging);
                            Log.d(TAG, "Correct the error displays charge status.");
                        }
                        break;
                }

                statusTextView.setText(statusString);
                pluggedTextView.setText(mPluggeString);
                voltageTextView.setText(Integer.toString(voltage) + " mv");
                if (ENABLE_BATTRY_TEMPERATURE) {
                    int batteryElectronic = Integer.parseInt(readFile(ENG_CHARGER_FGU_CURRENT_K414).trim()) / 1000;
                    mBatteryElectronicTextView.setText(batteryElectronic + " ma");

                    mBatteryTemperature = getDateFromNode(ENG_REAL_BATTERY_TEMPERATURE);
                    mTemperatureTextView.setText(mBatteryTemperature / 10 + " \u2103");
                }
                Log.v(STATUS, statusString);
                Log.v(PLUGGED, mPluggeString);
            }
        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        mHandler.removeCallbacks(mCompleteTest);
        // stopCharge();
        super.onDestroy();
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
        return batteryElectronic;
    }

    private void stopCharge() {
        boolean res = PhaseCheckParse.getInstance().writeChargeSwitch(1);
        Log.d(TAG, "stopCharge res="+res);
    }

    private void startCharge() {
        boolean res = PhaseCheckParse.getInstance().writeChargeSwitch(0);
        Log.d(TAG, "stopCharge res="+res);
    }
}
