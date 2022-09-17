package com.sprd.validationtools.background;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.sprd.validationtools.itemstest.wifi.WifiTestActivity;
import com.sprd.validationtools.modules.TestItem;
import com.sprd.validationtools.modules.UnitTestItemList;
import com.sprd.validationtools.utils.WifiTestUtil;

public class BackgroundWifiTest implements BackgroundTest {
	private static final String TAG = "BackgroundWifiTest";
    private WifiTestUtil wifiTestUtil = null;
    private int testResult = RESULT_INVALID;
    private Context mContext = null;

    private static final String TEST_CLASS_NAME = WifiTestActivity.class
            .getName();

    public BackgroundWifiTest(Context context) {
        mContext = context;
    }

    @Override
    public void startTest() {
        wifiTestUtil = new WifiTestUtil(
                (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE)) {

            public void wifiDeviceListChange(List<ScanResult> wifiDeviceList) {
                if (wifiDeviceList != null) {
                    testResult = RESULT_PASS;
                    BackgroundWifiTest.this.stopTest();
                }
            }
        };
        Log.d(TAG, "startTest begin!");
        wifiTestUtil.startTest(mContext);
    }

    @Override
    public void stopTest() {
    	Log.d(TAG, "stopTest!");
    	wifiTestUtil.stopTest();
    }

    @Override
    public int getResult() {
        return testResult;
    }

    @Override
    public String getResultStr() {
        String btResult = "Wifi:";
        if (RESULT_PASS == testResult) {
            btResult += "PASS";
        } else {
            btResult += "FAIL";
        }

        return btResult;
    }

    @Override
    public int getTestItemIdx() {
        return -1;
    }

    @Override
    public TestItem getTestItem(Context context) {
        return UnitTestItemList.getInstance(mContext).getTestItemByClassName(
                TEST_CLASS_NAME);
    }
}
