package com.sprd.validationtools.itemstest.telephony;

import java.util.ArrayList;
import java.util.List;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;
import com.simcom.testtools.R;

import android.os.Bundle;
import android.os.Handler;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SIMCardTestActivity extends BaseActivity {

    private LinearLayout container;
    private static final String TAG = "SIMCardTestActivity";

    private TelephonyManager telMgr;
    private int mSimReadyCount = 0;
    private int phoneCount;
    public Handler mHandler = new Handler();
    private int TIME_OUT = 10000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sim_card_test);
        setTitle(R.string.sim_card_test_tittle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        container = (LinearLayout) findViewById(R.id.sim_test_result_container);
        setTitle(R.string.sim_card_test_tittle);
        phoneCount = TelephonyManager.from(SIMCardTestActivity.this).getPhoneCount() > 0 ? 1 : 0;
        showDevice();
        /*SPRD bug 760913:Test can pass/fail must click button*/
        if(Const.isBoardISharkL210c10()){
            mPassButton.setVisibility(View.GONE);
        }
        /*@}*/
//        int phoneCount = TelephonyManager.from(SIMCardTestActivity.this).getPhoneCount();
        Log.d(TAG, "onCreate phoneCount="+phoneCount+",mSimReadyCount="+mSimReadyCount);
        if (phoneCount == mSimReadyCount) {
            mHandler.postDelayed(() -> {
                Toast.makeText(SIMCardTestActivity.this,
                        R.string.text_pass, Toast.LENGTH_SHORT).show();
                /*SPRD bug 760913:Test can pass/fail must click button*/
                if(Const.isBoardISharkL210c10()){
                    Log.d("", "isBoardISharkL210c10 is return!");
                    mPassButton.setVisibility(View.VISIBLE);
                    return;
                }
                /*@}*/
                storeRusult(true);
                finish();
            }, TIME_OUT);
        }else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SIMCardTestActivity.this, R.string.text_fail, Toast.LENGTH_SHORT).show();
                    /*SPRD bug 760913:Test can pass/fail must click button*/
                    if(Const.isBoardISharkL210c10()){
                        Log.d("", "isBoardISharkL210c10 is return!");
                        return;
                    }
                    /*@}*/
                    storeRusult(false);
                    finish();
                }
            }, TIME_OUT);
        }
        /*SPRD bug 760913:Test can pass/fail must click button*/
        if(Const.isBoardISharkL210c10()){
            //ignore
        }else{
            super.removeButton();
        }
        /*@}*/
    }

    private List<String> getResultList(int simId) {
        List<String> resultList = new ArrayList<String>();
        telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE + simId);
        if (telMgr == null) {
            telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (telMgr == null) {
                return null;
            }
        }
        int mSubIds[] = SubscriptionManager.getSubId(simId);
        if(mSubIds == null || mSubIds.length <= 0) return null;
        int mSubId = mSubIds[0];
        /* SPRD:436223 Sim test wrong in sigle sim in the phone@{*/
        if (telMgr.getSimState(simId) == TelephonyManager.SIM_STATE_READY) {
            resultList.add("fine");
            mSimReadyCount++;
        } else if (telMgr.getSimState(simId) == TelephonyManager.SIM_STATE_ABSENT) {
            resultList.add("no SIM card");
        } else {
            resultList.add("locked/unknow");
        }

        if (telMgr.getSimCountryIsoForPhone(simId).equals("")) {
            resultList.add("can not get country");
        } else {
            resultList.add(telMgr.getSimCountryIsoForPhone(simId));
        }

        if (telMgr.getSimOperatorNumericForPhone(simId).equals("")) {
            resultList.add("can not get operator");
        } else {
            resultList.add(telMgr.getSimOperatorNumericForPhone(simId));
        }

        if (telMgr.getSimOperatorNameForPhone(simId).equals("")) {
            resultList.add("can not get operator name");
        } else {
            resultList.add(telMgr.getSimOperatorNameForPhone(simId));
        }

        if (telMgr.getSimSerialNumber(mSubId) != null && SubscriptionManager.isValidSubscriptionId(mSubId)) {
            resultList.add(telMgr.getSimSerialNumber(mSubId));
        } else {
            resultList.add("can not get serial number");
        }

        if (telMgr.getSubscriberId(mSubId) != null && SubscriptionManager.isValidSubscriptionId(mSubId)) {
            resultList.add(telMgr.getSubscriberId(mSubId));
        } else {
            resultList.add("can not get subscriber id");
        }

        if (telMgr.getDeviceId(simId) != null) {
            resultList.add(telMgr.getDeviceId(simId));
        } else {
            resultList.add("can not get device id");
        }

        if (telMgr.getLine1Number(mSubId) != null && SubscriptionManager.isValidSubscriptionId(mSubId)) {
            resultList.add(telMgr.getLine1Number(mSubId));
        } else {
            resultList.add("can not get phone number");
        }

        if (telMgr.getPhoneType(simId) == 0) {
            resultList.add("NONE");
        } else if (telMgr.getPhoneType(simId) == 1) {
            resultList.add("GSM");
        } else if (telMgr.getPhoneType(simId) == 2) {
            resultList.add("CDMA");
        } else if (telMgr.getPhoneType(simId) == 3) {
            resultList.add("SIP");
        }
        /* @}*/

        if (telMgr.getDataState() == 0) {
            resultList.add("disconnected");
        } else if (telMgr.getDataState() == 1) {
            resultList.add("connecting");
        } else if (telMgr.getDataState() == 2) {
            resultList.add("connected");
        } else if (telMgr.getDataState() == 3) {
            resultList.add("suspended");
        }

        if (telMgr.getDataActivity() == 0) {
            resultList.add("none");
        } else if (telMgr.getDataActivity() == 1) {
            resultList.add("in");
        } else if (telMgr.getDataActivity() == 2) {
            resultList.add("out");
        } else if (telMgr.getDataActivity() == 3) {
            resultList.add("in/out");
        } else if (telMgr.getDataActivity() == 4) {
            resultList.add("dormant");
        }

        if (!telMgr.getNetworkCountryIsoForPhone(simId).equals("")) {
            resultList.add(telMgr.getNetworkCountryIsoForPhone(simId));
        } else {
            resultList.add("can not get network country");
        }

        if (!telMgr.getNetworkOperatorForPhone(simId).equals("")) {
            resultList.add(telMgr.getNetworkOperatorForPhone(simId));
        } else {
            resultList.add("can not get network operator");
        }

        if (telMgr.getNetworkType(mSubId) == 0) {
            resultList.add("unknow");
        } else if (telMgr.getNetworkType(mSubId) == 1) {
            resultList.add("gprs");
        } else if (telMgr.getNetworkType(mSubId) == 2) {
            resultList.add("edge");
        } else if (telMgr.getNetworkType(mSubId) == 3) {
            resultList.add("umts");
        } else if (telMgr.getNetworkType(mSubId) == 4) {
            resultList.add("hsdpa");
        } else if (telMgr.getNetworkType(mSubId) == 5) {
            resultList.add("hsupa");
        } else if (telMgr.getNetworkType(mSubId) == 6) {
            resultList.add("hspa");
        } else if (telMgr.getNetworkType(mSubId) == 7) {
            resultList.add("cdma");
        } else if (telMgr.getNetworkType(mSubId) == 8) {
            resultList.add("evdo 0");
        } else if (telMgr.getNetworkType(mSubId) == 9) {
            resultList.add("evdo a");
        } else if (telMgr.getNetworkType(mSubId) == 10) {
            resultList.add("evdo b");
        } else if (telMgr.getNetworkType(mSubId) == 11) {
            resultList.add("1xrtt");
        } else if (telMgr.getNetworkType(mSubId) == 12) {
            resultList.add("iden");
        } else if (telMgr.getNetworkType(mSubId) == 13) {
            resultList.add("lte");
        } else if (telMgr.getNetworkType(mSubId) == 14) {
            resultList.add("ehrpd");
        } else if (telMgr.getNetworkType(mSubId) == 15) {
            resultList.add("hspap");
        }
        return resultList;
    }

    private List<String> getKeyList() {
        List<String> keyList = new ArrayList<String>();
        keyList.add("Sim State:  ");
        keyList.add("Sim Country:  ");
        keyList.add("Sim Operator:  ");
        keyList.add("Sim Operator Name:  ");
        keyList.add("Sim Serial Number:  ");
        keyList.add("Subscriber Id:  ");
        keyList.add("Device Id:  ");
        keyList.add("Line 1 Number:  ");
        keyList.add("Phone Type:  ");
        keyList.add("Data State:  ");
        keyList.add("Data Activity:  ");
        keyList.add("Network Country:  ");
        keyList.add("Network Operator:  ");
        keyList.add("Network Type:  ");
        return keyList;
    }

    private void showDevice() {

        List<String> keyList = getKeyList();
        List<String> resultList0 = null;

        for (int i = 0; i < phoneCount; i++) {
            TextView tv = new TextView(this);
            if (i != 0) {
                tv.append("\n\n");
            }
            tv.append("Sim" + (i + 1) + " "
                    + this.getResources().getString(R.string.sim_test_result)
                    + ":\n");
            resultList0 = getResultList(i);
            for (int j = 0; j < 14; j++) {
                if (resultList0 != null)
                    tv.append(keyList.get(j) + resultList0.get(j) + "\n");
                else
                    tv.append(keyList.get(j) + "\n");
            }
            container.addView(tv);
        }
    }
}
