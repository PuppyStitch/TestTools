package com.sprd.validationtools.testinfo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.sprd.validationtools.Const;
import com.sprd.validationtools.PhaseCheckParse;
import com.sprd.validationtools.ValidationToolsMainActivity;
import com.sprd.validationtools.modules.TestItem;
import com.sprd.validationtools.modules.UnitTestItemList;
import com.sprd.validationtools.sqlite.EngSqlite;
import com.sprd.validationtools.utils.FileUtils;
import com.sprd.validationtools.utils.ValidationToolsUtils;

public class TestInfoQRCode {
    private static final String TAG = "TestInfoQRCode";

    private static final String LCD_ID_OLD = "/sys/devices/platform/soc/soc:ap-ahb/20800000.dispc/lcd_name";
    private static final String LCD_ID_NEW = "/sys/class/display/panel0/name";
    private static final String TP_CHIP_ID = "/sys/touchscreen/chip_id";
    private static final String TP_FIRMWARE_VERSION = "/sys/touchscreen/firmware_version";
    private static final String GSENSOR = "sys/module/sprd_phinfo/parameters/SPRD_GsensorInfo";
    private static final String LSENSOR = "sys/module/sprd_phinfo/parameters/SPRD_LsensorInfo";
    private static final String CAMERA_SENSOR = "/sys/devices/virtual/misc/sprd_sensor/camera_sensor_name";

    private static final String SENSOR_VERSION = "/sys/class/sprd_sensorhub/sensor_hub/sensor_info";

    private static final String TEXT_PASS = "PASS";
    private static final String TEXT_FAIL = "FAIL";

    private SharedPreferences mPrefs;
    private static final boolean SUPPORT_MATERIAL_INFO = false;

    private ArrayList<TestItem> mTestItemsArray = new ArrayList<TestItem>();

    public String getTestInfoQRCode(Context context) {
        try {
            PhaseCheckParse mCheckParse = PhaseCheckParse.getInstance();
            EngSqlite mEngSqlite = EngSqlite.getInstance(context);
            mTestItemsArray.clear();
            mTestItemsArray = mEngSqlite.queryData(UnitTestItemList.getInstance(context).getTestItemList());
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

            JSONObject root = new JSONObject();
            // SN1
            String sn1 = "", sn2 = "", imei1 = "", imei2 = "", mElapsedTime = "", mTestResult = "", mErrCode = "", mErrType = "", mErrMsg = "";
            //UID
            String uid = "";
            // set sn
            sn1 = mCheckParse.getSnByIndex(0);
            sn2 = mCheckParse.getSnByIndex(1);
            // set imei
            imei1 = ValidationToolsUtils.getImei(context, 0);
            imei2 = ValidationToolsUtils.getImei(context, 1);
            // Auto(Full) test mElapsedTime
            if (mPrefs != null) {
                mElapsedTime = mPrefs.getLong(
                        ValidationToolsMainActivity.FULL_TEST_USED_TIME, 0)
                        / 1000 + "s";
            }
            // MMI test result(all pass/fail)
            if (mEngSqlite.queryFailCount() == 0
                    && mEngSqlite.queryNotTestCount() == 0) {
                mTestResult = TEXT_PASS;
            } else {
                mTestResult = TEXT_FAIL;
            }
            // First error code/name/des
            JSONArray mTestDetail = new JSONArray();
            int code = 0;
            boolean findError = false;
            for (TestItem testItem : mTestItemsArray) {
                JSONObject testItemJSONObject = new JSONObject();
                String result = testItem.getTestResult() == Const.DEFAULT ? "UnTest"
                        : testItem.getTestResult() == Const.SUCCESS ? "PASS"
                                : "FAIL";
                Log.d(TAG, "testItem result = " + result);
                testItemJSONObject.put(testItem.getTestName(), result);
                mTestDetail.put(testItemJSONObject);
                //error code
                Log.d(TAG, "getTestname = " + testItem.getTestName() + "\n");
                code++;
                if (testItem.getTestResult() != Const.SUCCESS && !findError) {
                    Log.d(TAG, "getTestname test code = " + code + "\n");
                    mErrType = testItem.getTestClassName();
                    mErrMsg = testItem.getTestName();
                    mErrCode = "MMI" + code;
                    Log.d(TAG, "getTestname test mErrCode = " + mErrCode
                            + ",mErrType="+mErrType+",mErrMsg="+mErrMsg);
                    findError = true;
                }
            }
            if(ValidationToolsUtils.ENABLE_UID){
                uid = ValidationToolsUtils.getUid();
            }
            Log.d(TAG, "sn1 = " + sn1 + ",sn2=" + sn2);
            Log.d(TAG, "imei1 = " + imei1 + ",imei2 = " + imei2 + ",uid= " + uid);
            Log.d(TAG, "mElapsedTime = " + mElapsedTime + ",mTestResult=" + mTestResult);

            // Add to json
            root.put("SN1", sn1);
            root.put("SN2", sn2);
            root.put("IMEI1", imei1);
            root.put("IMEI2", imei2);
            root.put("UID", uid);
            root.put("ElapsedTime", mElapsedTime);
            root.put("TestResult", mTestResult);
            root.put("ErrCode", mErrCode);
            root.put("ErrType", mErrType);
            root.put("ErrMsg", mErrMsg);

            // TestDetail
            root.put("TestDetail", mTestDetail);

            // MaterialInfo
            if(SUPPORT_MATERIAL_INFO){
                String[] MaterialInfoNames = { "Camera", "RAM", "ROM", "TP", "LCD" };

                // Read Camera info
                String cameraInfo = getCameraID().trim();
                Log.d(TAG, "cameraInfo = " + cameraInfo);
                // Read TP info
                String tpInfo = getTpID().trim() + "\n";
                tpInfo += getTpVersion().trim();
                Log.d(TAG, "tpInfo = " + tpInfo );
                // Read LCD info
                String lcdInfo = getLcdID().trim();
                Log.d(TAG, "lcdInfo = " + lcdInfo);

                String[] MaterialInfo = { cameraInfo, "RAM", "ROM", tpInfo, lcdInfo };
                JSONArray mMaterialInfo = new JSONArray();
                for (int i = 0; i < MaterialInfoNames.length; i++) {
                    JSONObject MaterialItem = new JSONObject();
                    MaterialItem.put(MaterialInfoNames[i], MaterialInfo[i]);
                    mMaterialInfo.put(MaterialItem);
                }
                root.put("MaterialInfo", mMaterialInfo);
            }

            Log.d(TAG, "getTestInfoQRCode:\n" + root.toString());
            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getLcdID() {
        String lcdId = FileUtils.fileIsExists(LCD_ID_OLD) ? LCD_ID_OLD
                : LCD_ID_NEW;
        return FileUtils.readFile(lcdId);
    }

    private String getCameraID() {
        String result = FileUtils.readFile(CAMERA_SENSOR);
        if (TextUtils.isEmpty(result)) {
            result = SystemProperties.get("vendor.cam.sensor.info");
            return result;
        }
        return result;
    }

    private String getLsensorID() {
        return FileUtils.readFile(LSENSOR);
    }

    private String getGsensorID() {
        return FileUtils.readFile(GSENSOR);
    }

    private String getTpID() {
        return FileUtils.readFile(TP_CHIP_ID);
    }

    private String getTpVersion() {
        return FileUtils.readFile(TP_FIRMWARE_VERSION);
    }
}
