
package com.sprd.validationtools;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest.Key;
import android.hardware.display.DisplayManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.ViewConfiguration;

import com.sprd.validationtools.itemstest.*;
import com.sprd.validationtools.itemstest.ai.AITest;
import com.sprd.validationtools.itemstest.audio.SmartPATest;
import com.sprd.validationtools.itemstest.audio.SoundTriggerTestActivity;
import com.sprd.validationtools.itemstest.camera.CameraTestActivity;
import com.sprd.validationtools.itemstest.camera.ColorTemperatureTestActivty;
import com.sprd.validationtools.itemstest.camera.FrontCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.FrontSecondaryCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.MacroLensCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.SecondaryCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.SpwCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.TofCalibrationTest;
//import com.sprd.validationtools.itemstest.fingerprint.FingerprintTestActivity;
import com.sprd.validationtools.itemstest.fm.FMTest;
import com.sprd.validationtools.itemstest.gps.GpsTestActivity;
import com.sprd.validationtools.itemstest.led.BlueLightTest;
import com.sprd.validationtools.itemstest.led.GreenLightTest;
import com.sprd.validationtools.itemstest.led.RedLightTest;
import com.sprd.validationtools.itemstest.nfc.NFCTestActivity;
import com.sprd.validationtools.itemstest.otg.OTGTest;
import com.sprd.validationtools.itemstest.sensor.ASensorCalibrationActivity;
import com.sprd.validationtools.itemstest.sensor.CompassTestActivity;
import com.sprd.validationtools.itemstest.sensor.GSensorCalibrationActivity;
import com.sprd.validationtools.itemstest.sensor.GyroscopeTestActivity;
import com.sprd.validationtools.itemstest.sensor.LsensorNoiseTestActivity;
import com.sprd.validationtools.itemstest.sensor.MSensorCalibrationActivity;
import com.sprd.validationtools.itemstest.sensor.MagneticTestActivity;
import com.sprd.validationtools.itemstest.sensor.PressureTestActivity;
import com.sprd.validationtools.itemstest.sensor.ProxSensorCalibrationActivity;
import com.sprd.validationtools.itemstest.sensor.PsensorTestActivity;
import com.sprd.validationtools.itemstest.tp.MutiTouchDoubleScreenTest;
import com.sprd.validationtools.itemstest.tp.MutiTouchTest;
import com.sprd.validationtools.itemstest.tp.SingleTouchPointTest;
import com.sprd.validationtools.utils.FileUtils;
import com.sprd.validationtools.utils.Native;
import com.sprd.validationtools.utils.ValidationToolsUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

public class Const {
    private static String TAG = "Const";

    public static boolean DEBUG = true;

    //This DIR while be built in (device\sprd\(BOARD)\common\rootdir\root\init.common.rc)
    public static final String PRODUCTINFO_DIR = "/mnt/vendor/productinfo";

    public static final String LED_PATH = "/sys/class/leds/red/brightness";
    public static final String CALIBRATOR_CMD = "/sys/class/sprd_sensorhub/sensor_hub/calibrator_cmd";
    public static final String CALIBRATOR_DATA = "/sys/class/sprd_sensorhub/sensor_hub/calibrator_data";
    public static final String CAMERA_FLASH = "/sys/devices/virtual/misc/sprd_flash/test";

    public static final String RESULT_TEST_NAME = "result";

    public static final String INTENT_PARA_TEST_NAME = "testname";
    public static final String INTENT_PARA_TEST_INDEX = "testindex";
    public static final String INTENT_BACKGROUND_TEST_RESULT = "bgtestresult";
    public static final String INTENT_RESULT_TYPE = "resulttype";
    public static final String INTENT_PARA_TEST_CLASSNAME = "test_classname";

    public static final int RESULT_TYPE_FOR_SYSTEMTEST = 0;
    public static final int RESULT_TYPE_NORMAL = 1;

    public static final int EXT_EMULATED_PATH = 0;
    public static final int EXT_COMMON_PATH = 1;
    public static final int OTG_UDISK_PATH = 2;

    public final static int TEST_ITEM_DONE = 0;
    public static final boolean IS_SUPPORT_LED_TEST = FileUtils.fileIsExists(LED_PATH);
    public static final boolean IS_SUPPORT_CALIBTATION_TEST = FileUtils.fileIsExists(CALIBRATOR_CMD);

    public static final boolean DISABLE_MSENSOR_CALI = true;
    /*SRPD bug 776983:Add OTG*/
    public static final String OTG_PATH = "/sys/class/dual_role_usb/sprd_dual_role_usb/supported_modes";
    /*@}*/
    public static final String OTG_PATH_k414 = "/sys/class/typec/port0/port_type";

    public static final boolean IS_SUPPORT_DUALCAMERA_CALIBRATION = isSupportBoardByName("sp9832e_1h10") || true;

    /* SPRD Bug 746453:Some product don't support blue indicator light. @{ */
    public static final boolean DISABLE_BLUE_LED = isBoardISharkL210c10();
    /*@}*/
    /*SPRD bug 855450:ZTE feature*/
    public static final String SECURITY_CODE = "SECURITY_CODE";
    /*@}*/
    public static final String LED_RED_PATH = "/sys/class/leds/sc27xx:red/brightness";
    public static final String LED_BLUE_PATH = "/sys/class/leds/sc27xx:blue/brightness";
    public static final String LED_GREEN_PATH = "/sys/class/leds/sc27xx:green/brightness";

    // add status for test item
    public static final int FAIL = 0;
    public static final int SUCCESS = 1;
    public static final int DEFAULT = 2;

    private static boolean mSupportLightSensor = true;
    public static final String CAMERA_CALI_VERI = "persist.vendor.cam.multicam.cali.veri";

    public static boolean isSupportCameraCaliVeri(){
        String calibraionSupport = SystemProperties.get(CAMERA_CALI_VERI,"0");
        Log.d(TAG, "isSupportCameraCaliVeri calibraionSupport="+ calibraionSupport);
        return calibraionSupport.equals("1");
    }

    // add the filter here
    public static boolean isSupport(String className, Context context) {
        Log.d(TAG, "isSupport className="+className);
        DisplayManager mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        if (FrontCameraTestActivity.class.getName().equals(className)) {
            int mNumberOfCameras = Camera.getNumberOfCameras();
            CameraInfo[] mInfo = new CameraInfo[mNumberOfCameras];
            for (int i = 0; i < mNumberOfCameras; i++) {
                mInfo[i] = new CameraInfo();
                Camera.getCameraInfo(i, mInfo[i]);
                if (mInfo[i].facing == CameraInfo.CAMERA_FACING_FRONT) {
                    return true;
                }
            }

            return false;
        //TofCalibrationTest
        } else if (TofCalibrationTest.class.getName().equals(className)) {
            return ValidationToolsUtils.get_camera_sensor_tof_support_flag();
        } else if (AITest.class.getName().equals(className)) {
            String ipFlag = SystemProperties.get("persist.vendor.npu.version", "0");
            Log.d(TAG, "ipFlag=:" + ipFlag);
            if (ipFlag.equals("0")) {
                return false;
            } else {
                return true;
            }
        } else if (SmartPATest.class.getName().equals(className)) {
            AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            String supportResult = mAudioManager.getParameters("SmartAmpCalibration");
            Log.d(TAG, "supportResult=:" + supportResult);
            if (TextUtils.isEmpty(supportResult) || supportResult.contains("unsupport")) {
                return false;
            }
            return true;
        } else if (CameraTestActivity.class.getName().equals(className)) {
            int mNumberOfCameras = Camera.getNumberOfCameras();
            CameraInfo[] mInfo = new CameraInfo[mNumberOfCameras];
            for (int i = 0; i < mNumberOfCameras; i++) {
                mInfo[i] = new CameraInfo();
                Camera.getCameraInfo(i, mInfo[i]);
                if (mInfo[i].facing == CameraInfo.CAMERA_FACING_BACK) {
                    return true;
                }
            }

            return false;
        } else if (OTGTest.class.getName().equals(className)) {
            if(isSupportOTG()){
                return true;
            }
            if(ValidationToolsUtils.isSupportInPCBAConf("OTG")){
                return true;
            }
            return false;
            /*@}*/
        } else if (CompassTestActivity.class.getName().equals(className)) {
            return false;
        } else if (PsensorTestActivity.class.getName().equals(className)) {
            SensorManager sensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) == null) {
                Log.d(TAG, "Not support light sensor!");
                mSupportLightSensor = false;
            }
            if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) == null) {
                return false;
            }
        } else if (GyroscopeTestActivity.class.getName().equals(className)) {
            SensorManager sensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null) {
                return false;
            }
        } else if (MagneticTestActivity.class.getName().equals(className)) {
            SensorManager sensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null) {
                return false;
            }
        } else if (PressureTestActivity.class.getName().equals(className)) {
            SensorManager sensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) == null) {
                return false;
            }
        } else if (ASensorCalibrationActivity.class.getName().equals(className)) {
            SensorManager sensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null
                    || !IS_SUPPORT_CALIBTATION_TEST) {
                return false;
            }
        } else if (GSensorCalibrationActivity.class.getName().equals(className)) {
            SensorManager sensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null
                    || !IS_SUPPORT_CALIBTATION_TEST) {
                return false;
            }
        /** BEGIN BUG479359 zhijie.yang 2016/5/5 MMI add the magnetic sensors and the prox sensor calibration**/
        } else if (MSensorCalibrationActivity.class.getName().equals(className)) {
            if (DISABLE_MSENSOR_CALI) {
                return false;
            }
        } else if (ProxSensorCalibrationActivity.class.getName().equals(className)) {
            SensorManager sensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) == null
                    || !IS_SUPPORT_CALIBTATION_TEST) {
                return false;
            }
        /*BEGIN BUG555701 zhijie.yang 2016/05/21*/
        } else if (SecondaryCameraTestActivity.class.getName().equals(className)) {
            int mNumberOfCameras = Camera.getNumberOfCameras();
            /* SPRD bug 751615:Add for multi camera */
            String cam3type = SystemProperties.get("persist.sys.cam3.type",
                    "unknown");
            Log.d(TAG, "SecondaryCameraTestActivity 1 mNumberOfCameras="
                    + mNumberOfCameras + ",cam3type=" + cam3type);
            if (mNumberOfCameras <= 2) {
                return false;
            }
            boolean calibraionSupportFlag = false;
            String calibraionSupport = SystemProperties.get("persist.sys.3d.calibraion",
                    "0");
            Log.d(TAG, "SecondaryCameraTestActivity calibraionSupport="+ calibraionSupport);
            if(calibraionSupport != null && calibraionSupport.equals("1")){
                calibraionSupportFlag = true;
            }
            if (mNumberOfCameras >= 3) {
                if ((cam3type.equals("back_blur") || cam3type
                        .equals("back_sbs")) || calibraionSupportFlag) {
                    return true;
                }else{
                    return false;
                }
            }
        }
        else if (FrontSecondaryCameraTestActivity.class.getName().equals(className)) {
            int mNumberOfCameras = Camera.getNumberOfCameras();
            /* SPRD bug 751615:Add for multi camera */
            String cam3type = SystemProperties.get("persist.sys.cam3.type",
                    "unknown");
            Log.d(TAG, "FrontSecondaryCameraTestActivity mNumberOfCameras="
                    + mNumberOfCameras + ",cam3type=" + cam3type);
            if (mNumberOfCameras <= 2) {
                return false;
            }
            /*SPRD bug 759782 : Change filter*/
            if (mNumberOfCameras >= 3) {
                if (cam3type.equals("front_blur")
                        || cam3type.equals("front_sbs")) {
                    return true;
                }else{
                    return false;
                }
            }
        }
        else if (SoundTriggerTestActivity.class.getName().equals(className)) {
            if(!isWhale2Support() || isSharkL2Support()){
                return false;
            }
        } else if (NFCTestActivity.class.getName().equals(className)) {
            return isSupportNfc(context);
//        } else if (FingerprintTestActivity.class.getName().equals(className)) {
//            boolean support = isSupportFingerPrint(context);
//            Log.d(TAG, "isSupportFingerPrint support:" + support);
//            return support;
        } else if (GpsTestActivity.class.getName().equals(className)) {
            boolean result = isSupportGPS(context);
            Log.d(TAG, "isSupportGPS:" + result);
            return result;
        }
        /*END BUG555701 zhijie.yang 2016/05/21*/
        /** END BUG479359 zhijie.yang 2016/5/5 MMI add the magnetic sensors and the prox sensor calibration**/
        //SPRD: Modify for bug538349, open the RGB color indicator test.
        else if (RedLightTest.class.getName().equals(className)) {
            if (!IS_SUPPORT_LED_TEST && !FileUtils.fileIsExists(LED_RED_PATH)) {
                return false;
            }
        } else if (GreenLightTest.class.getName().equals(className)) {
            if (!IS_SUPPORT_LED_TEST && !FileUtils.fileIsExists(LED_GREEN_PATH)) {
                return false;
            }
        } else if (BlueLightTest.class.getName().equals(className)) {
            if (!IS_SUPPORT_LED_TEST && !FileUtils.fileIsExists(LED_BLUE_PATH)) {
                return false;
            }
            /* SPRD Bug 746453:Some product don't support blue indicator light. @{ */
            if (DISABLE_BLUE_LED) {
                return false;
            }
            /*@}*/
        }
        /*SPRD bug 743720:Add LsensorNoiseTestActivity*/
        else if (LsensorNoiseTestActivity.class.getName().equals(className)) {
            SensorManager sensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) == null) {
                Log.d(TAG, "LsensorNoiseTestActivity false");
                return false;
            }
            /*SPRD bug 820257:Display test item by file exists.*/
            if(FileUtils.fileIsExists(LsensorNoiseTestActivity.SensorNoiseFile)){
                Log.d(TAG, "LsensorNoiseTestActivity true");
                return true;
            }
            Log.d(TAG, "LsensorNoiseTestActivity false");
            return false;
            /*@}*/
        }
        /*@}*/
        else if (SingleTouchPointTest.class.getName().equals(className)) {
            if(Const.isBoardISharkL210c10()){
                Log.d(TAG, "SingleTouchPointTest not support!");
                return false;
            }
        }
        else if (ColorTemperatureTestActivty.class.getName().equals(className)) {
            if(isSupportColorTemplture()){
                return true;
            }else{
                return false;
            }
        }
        else if (SpwCameraTestActivity.class.getName().equals(className)) {
            int mNumberOfCameras = Camera.getNumberOfCameras();
            Log.d(TAG, "SpwCameraTestActivity mNumberOfCameras="+ mNumberOfCameras);
            if (mNumberOfCameras <= 3) {
                return false;
            }else{
                return true;
            }
        }
        else if (MacroLensCameraTestActivity.class.getName().equals(className)) {
            int mNumberOfCameras = Camera.getNumberOfCameras();
            Log.d(TAG, "MacroLensCameraTestActivity mNumberOfCameras="+ mNumberOfCameras);
            if (mNumberOfCameras <= 4) {
                return false;
            }else{
                return true;
            }
        }
        else if (FMTest.class.getName().equals(className)) {
            String disableFm = SystemProperties.get("ro.vendor.validationtools.fmdisable", "0");
            Log.d(TAG, "FMTest disableFm="+ disableFm);
            if(disableFm != null && disableFm.equals("1")){
                return false;
            }
        }
        else if (MutiTouchDoubleScreenTest.class.getName().equals(className)) {
            if(displays.length <= 1){
                return false;
            }
            Log.d(TAG, "MutiTouchDoubleScreenTest = true");
        }
        else if ( MutiTouchTest.class.getName().equals(className)) {
            if(displays.length > 1){
                return false;
            }
            Log.d(TAG, "MutiTouchTest = true");
        }
        return true;
    }

    public static final int CAMERA_SPW_FEATURE = 7;
    private static final  CameraCharacteristics.Key<Integer> ANDROID_SPRD_SPW_CAMERA_ID = new CameraCharacteristics.Key<Integer>("com.addParameters.sprdUltraWideId", Integer.class);
    private static final CameraCharacteristics.Key<int[]> ANDROID_SPRD_FEATURE_LIST = new CameraCharacteristics.Key<int[]>("com.addParameters.sprdCamFeatureList", int[].class);
    private static boolean isEnableSPW(Context context) {
        boolean isEnableSPW = false;
        CameraManager cameraManager = null;
        boolean isLOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                || "L".equals(Build.VERSION.CODENAME) || "LOLLIPOP".equals(Build.VERSION.CODENAME);
        Log.d(TAG,"isLOrHigher is "+isLOrHigher);
        try {
            Object service = isLOrHigher ?  context.getSystemService(Context.CAMERA_SERVICE)
                    : null;
            cameraManager = (CameraManager) service;
            int[] featureList  = cameraManager.getCameraCharacteristics(Integer.toString(0)).get(ANDROID_SPRD_FEATURE_LIST);
            if (featureList == null || featureList.length == 0) {
                Log.d(TAG,"HAL does not have feature list");
                SystemProperties.set("persist.sys.spw.enable", "0");
                return false;
            }
            isEnableSPW = featureList[CAMERA_SPW_FEATURE] == 1 ? true : false;
            Log.d(TAG,"SPW feature enable from HAL is "+isEnableSPW);
            if (isEnableSPW) {
                int cameraId = cameraManager.getCameraCharacteristics(Integer.toString(0)).get(
                        ANDROID_SPRD_SPW_CAMERA_ID);
                SystemProperties.set("persist.sys.spw.cameraid", String.valueOf(cameraId));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemProperties.set("persist.sys.spw.enable", "0");
            return false;
        }
        SystemProperties.set("persist.sys.spw.enable",(isEnableSPW == true ? "1" :"0"));
        return isEnableSPW;
    }
    protected static boolean isSupportColorTemplture() {
        return ValidationToolsUtils.get_TARGET_CAMERA_SENSOR_CCT_TCS3430_flag();
    }

    private static boolean isSupportOTG(){
        BufferedReader bReader = null;
        InputStream inputStream = null;

        if(FileUtils.fileIsExists(OTG_PATH_k414)){
            try {
                Log.d(TAG, "isSupportOTG  OTG_PATH:"+OTG_PATH_k414);
                inputStream = new FileInputStream(
                        OTG_PATH_k414);
                bReader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
                String str = bReader.readLine();
                Log.d(TAG, "isSupportOTG  str:"+str);
                if (str != null && str.contains("[dual] source sink")) {
                    return true;
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "isSupportOTG()  Exception happens:");
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                Log.e(TAG, "isSupportOTG()  Exception happens:");
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (bReader != null) {
                        bReader.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "getSupportList()  Exception happens:");
                    e.printStackTrace();
                }
            }
        }else{
            try {
                Log.d(TAG, "OTGTest  OTG_PATH:"+OTG_PATH);
                inputStream = new FileInputStream(
                        OTG_PATH);
                bReader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
                String str = bReader.readLine();
                Log.d(TAG, "OTGTest  str:"+str);
                if (str != null && str.contains("ufp")) {
                    return true;
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "getSupportList()  Exception happens:");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "getSupportList()  Exception happens:");
                e.printStackTrace();
            } finally {
                try {
                    if (bReader != null) {
                        bReader.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean isCameraSupport() {
        int mNumberOfCameras = Camera.getNumberOfCameras();
        CameraInfo[] mInfo = new CameraInfo[mNumberOfCameras];
        for (int i = 0; i < mNumberOfCameras; i++) {
            mInfo[i] = new CameraInfo();
            Camera.getCameraInfo(i, mInfo[i]);
            if (mInfo[i].facing == CameraInfo.CAMERA_FACING_BACK) {
                return true;
            }
        }

        return false;
    }

    public static boolean isHomeSupport(Context context) {
        boolean isSupport = ViewConfiguration.get(context)
                .hasPermanentMenuKey();
        Log.d(TAG, "hw home is support:" + isSupport);
        return true;
    }

    public static boolean isBackSupport(Context context) {
        boolean isSupport = ViewConfiguration.get(context)
                .hasPermanentMenuKey();
        Log.d(TAG, "hw Back is support:" + isSupport);
        return true;
    }

    public static boolean isMenuSupport(Context context) {
        boolean isSupport = ViewConfiguration.get(context)
                .hasPermanentMenuKey();
        Log.d(TAG, "hw menu is support:" + isSupport);
        return true;
    }

    public static boolean isVolumeUpSupport() {
        boolean isSupport = SystemProperties.getBoolean(
                "ro.config.hw.vol_up_support", true);
        Log.d(TAG, "hw VolumeUp is support:" + isSupport);
        return isSupport;
    }

    public static boolean isVolumeDownSupport() {
        boolean isSupport = SystemProperties.getBoolean(
                "ro.config.hw.vol_down_support", true);
        Log.d(TAG, "hw VolumeDown is support:" + isSupport);
        return isSupport;
    }

    /*
    * SPRD:Modify Bug 537923, Judgment is not whale 2
    * @{
    */
    public static boolean isWhale2Support() {
        String hardware = SystemProperties.get("ro.boot.hardware", "unknown");
        /**BEGIN Bug 558940 zhijie.yang 2016/5/3 modify:phone loopback test fail **/
        if (hardware.contains("9860")) {
            return true;
        }
        return false;
    }

    public static boolean isIWhale2Support() {
        String hardware = SystemProperties.get("ro.boot.hardware", "unknown");
        if (hardware.contains("9861")) {
            return true;
        }
        return false;
    }

    public static boolean isSupportMeidShow() {
        String board = SystemProperties.get("ro.product.name", "unknown");
        Log.d(TAG, "isSupportMeidShow board=:" + board);
        if (board != null && board.startsWith("ums312_1h10_ctcc")) {
            return true;
        }
        if (board != null && board.startsWith("ums312_2h10_ctcc")) {
            return true;
        }
        if (board != null && board.startsWith("ums312_20c10_ctcc")) {
            return true;
        }
        return false;
    }

    public static boolean isSharkL2Support() {
        String hardware = SystemProperties.get("ro.boot.hardware", "unknown");
        if (hardware.contains("9850")) {
            return true;
        }
        return false;
    }

    /*SRPD bug 762371:Add new function*/
    public static boolean isSupportBoardByName(String boardName) {
        String board = SystemProperties.get("ro.product.board", "unknown");
        Log.d(TAG, "isSupportBoardByName board="+board+",boardName="+boardName);
        if (board != null && board.equals(boardName)) {
            return true;
        }
        return false;
    }
    /*@}*/

    public static boolean isSupportNfc(Context context) {
        boolean result = false;
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null) {
            result = true;
        }
        return result;
    }

    public static boolean isRefMicSupport() {
        return !SystemProperties.getBoolean("ro.factory.remove.refmic", false);
    }

    public static boolean isSupportGPS(Context context) {
        LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(mgr == null)
            return false;
        List<String> providers = mgr.getAllProviders();
        if(providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    public static boolean isBoardISharkL210c10() {
        return false;
    }

    public static boolean isSupportFeaturePhone(){
        return /*isSupportMacroByName(MACRO_NAME_FEATURE_PHONE_SUPPORT)*/false;
    }
    public static final String MACRO_NAME_FEATURE_PHONE_SUPPORT = "BOARD_FEATUREPHONE_CONFIG";
    public static boolean isSupportMacroByName(String macroName){
        Log.d(TAG, "isSupportMacroByName macroName="+macroName);
        boolean support = false;
        try {
            support = Native.native_is_support_macro(macroName) == 1;
            Log.d(TAG, "isSupportMacroByName support="+support);
            return support;
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }catch (UnknownError e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSupportFingerPrint(Context context) {
        final PackageManager pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            return true;
        }
        return false;
    }
}
