package com.sprd.validationtools.background;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.sprd.validationtools.itemstest.bt.BluetoothTestActivity;
import com.sprd.validationtools.modules.TestItem;
import com.sprd.validationtools.modules.UnitTestItemList;
import com.sprd.validationtools.utils.BtTestUtil;

public class BackgroundBtTest implements BackgroundTest {
    private static final String TAG = "BackgroundBtTest";
    private BtTestUtil mBtTestUtil = null;
    private int testResult = RESULT_INVALID;
    private Context mContext = null;

    private static final String TEST_CLASS_NAME = BluetoothTestActivity.class
            .getName();

    public BackgroundBtTest(Context context) {
        mContext = context;
    }

    @Override
    public void startTest() {
        mBtTestUtil = new BtTestUtil() {
            boolean mIsPass = false;

            public void btDeviceListAdd(BluetoothDevice device) {

                if (device != null) {
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        testResult = RESULT_PASS;
                        mIsPass = true;
                        BackgroundBtTest.this.stopTest();
                    }
                }
            }

            public void btDiscoveryFinished() {
                if (!mIsPass) {
                    testResult = RESULT_FAIL;
                    BackgroundBtTest.this.stopTest();
                }
            }
        };
        Log.d(TAG, "startTest begin!");
        mBtTestUtil.startTest(mContext);
    }

    @Override
    public void stopTest() {
        Log.d(TAG, "stopTest!");
        mBtTestUtil.stopTest();
    }

    @Override
    public int getResult() {
        return testResult;
    }

    @Override
    public String getResultStr() {
        String btResult = "BlueTooth:";
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
