package com.sprd.validationtools.background;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sprd.validationtools.itemstest.telephony.SIMCardTestActivity;
import com.sprd.validationtools.modules.TestItem;
import com.sprd.validationtools.modules.UnitTestItemList;

public class BackgroundSimTest implements BackgroundTest {
	private static final String TAG = "BackgroundSimTest";
    private int testResult = RESULT_INVALID;
    private Context mContext = null;

    private static final String TEST_CLASS_NAME = SIMCardTestActivity.class
            .getName();

    public BackgroundSimTest(Context context) {
        mContext = context;
    }

    @Override
    public void startTest() {
        new Thread() {
            public void run() {
                int phoneCount = TelephonyManager.from(mContext)
                        .getPhoneCount();
                TelephonyManager telMgr = null;
                int readyCount = 0;

                // modify 336688 by sprd
                // Single card and Multi card get TelephonyManager method is
                // different
                if (phoneCount == 1) {
                    telMgr = (TelephonyManager) mContext
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    if (telMgr.getSimState() == TelephonyManager.SIM_STATE_READY) {
                        readyCount++;
                    }
                    /* SPRD:394857 system test wrong in sim background test @{ */
                } else {
                    telMgr = (TelephonyManager) mContext
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    for (int i = 0; i < phoneCount; i++) {
                        if (telMgr.getSimState(i) == TelephonyManager.SIM_STATE_READY) {
                            readyCount++;
                        }
                    }
                }
                /* @} */
                Log.d(TAG, "startTest readyCount="+ readyCount+",phoneCount="+phoneCount);
                if (readyCount == phoneCount) {
                    testResult = RESULT_PASS;
                    stopTest();
                }
            }
        }.start();
    }

    @Override
    public void stopTest() {
    }

    @Override
    public int getResult() {
        return testResult;
    }

    @Override
    public String getResultStr() {
        String btResult = "Sim:";
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
