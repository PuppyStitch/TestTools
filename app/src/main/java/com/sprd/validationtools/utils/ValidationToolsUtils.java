package com.sprd.validationtools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import android.content.Context;
import android.media.AudioManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.WindowManagerGlobal;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class ValidationToolsUtils {
    private static final String TAG = "ValidationToolsUtils";
    private static final String AUDIO_WHALE_HAL_PROP = "ro.vendor.mmi.use.audio.whale.hal";
    private static final String CAMERA_SENSOR_CCT = "ro.vendor.mmi.camera.sensor.cct";
    private static final String CAMERA_SENSOR_TOF = "ro.vendor.mmi.camera.sensor.tof";
    private static final String ENG_PCBA_SUPPORT_CONFIG = "/vendor/etc/PCBA.conf";

    //Disable UID after androidQ:bug1203015
    public static final boolean ENABLE_UID = false;
    private static final String UID_PATH_1 = "sys/class/misc/sprd_efuse_otp/uid";
    private static final String UID_PATH_2 = "sys/class/misc/sprd_otp_ap_efuse/uid";
    //Is support AI key:cat modalias has 2FE
    private static final String KEY_INPUT_MODALIAS_PATH = "/sys/class/input/input2/modalias";
    private static final String AI_KEY_MODALIAS_VALUE = "2FE";

    public static String getUid(){
        String uidText = "";
        if(FileUtils.fileIsExists(UID_PATH_1)) {
            String uid = FileUtils.readFile(UID_PATH_1);
            if(uid != null && uid.contains(":")) {
                uidText = uid.substring(uid.indexOf(":") + 1);
            } else {
                uidText = uid;
            }
        } else {
            String uid = FileUtils.readFile(UID_PATH_2);
            if(uid != null && uid.contains(":")) {
                uidText = uid.substring(uid.indexOf(":") + 1);
            } else {
                uidText = uid;
            }
        }
        Log.d(TAG, "getUID uidText:" + uidText);
        return uidText;
    }

    public static String getImei(Context context, int index) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        int phoneCnt = tm.getPhoneCount();
        Log.d(TAG, "getIMEI===phoneCnt: " + phoneCnt + ",index=" + index);
        String imei = "";

        if (index >= 0 && index < phoneCnt) {
            imei = tm.getDeviceId(index);
            return imei;
        }
        return "";
    }

    public static boolean isFileExist(String path) {
        File file = new File(path);
        if (file != null && file.exists()) {
            return true;
        }
        return false;
    }


    public static boolean isSupportAGDSP(Context context) {
        boolean support = false;
        try {
            AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            String ret = audioManager.getParameters("isAudioDspExist");
            Log.i(TAG, "isAudioDspExist =" + ret);
            support = ret.indexOf("=1") != -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return support;
    }

    public static boolean getAudioWhaleLoopbackFlag() {
        String flagValue = "";
        try {
            flagValue = SystemProperties.get(AUDIO_WHALE_HAL_PROP, "");
            Log.d(TAG, "getAudioWhaleLoopbackFlag flagValue=" + flagValue
                    + ",AUDIO_WHALE_HAL_PROP=" + AUDIO_WHALE_HAL_PROP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !TextUtils.isEmpty(flagValue) && flagValue.equals("1");
    }

    public static boolean get_camera_sensor_tof_support_flag() {
        String flagValue = "";
        try {
            flagValue = SystemProperties.get(CAMERA_SENSOR_TOF, "");
            Log.d(TAG, "get_camera_sensor_tof_support_flag flagValue="
                    + flagValue + ",CAMERA_SENSOR_TOF=" + CAMERA_SENSOR_TOF);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !TextUtils.isEmpty(flagValue) && flagValue.equals("tof_vl53l0");
    }

    public static boolean get_TARGET_CAMERA_SENSOR_CCT_TCS3430_flag() {
        String flagValue = "";
        try {
            flagValue = SystemProperties.get(CAMERA_SENSOR_CCT, "");
            Log.d(TAG,
                    "native_get_TARGET_CAMERA_SENSOR_CCT_TCS3430_flag flagValue="
                            + flagValue + ",CAMERA_SENSOR_CCT="
                            + CAMERA_SENSOR_CCT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !TextUtils.isEmpty(flagValue) && flagValue.equals("ams_tcs3430");
    }

    public static boolean hasNavigationBar(Context context) {
        try {
            return WindowManagerGlobal.getWindowManagerService()
                    .hasNavigationBar(context.getDisplayId());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /*
     * Disable the Soundeffect for Phoneloopback,return the last status
     */
    public static boolean setSoundEffect(Context context, boolean isOn) {
        AudioManager audioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        if (isOn) {
            audioManager.loadSoundEffects();
        } else {
            audioManager.unloadSoundEffects();
        }
        boolean lastStatus = (Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.SOUND_EFFECTS_ENABLED, 0) == 1);
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SOUND_EFFECTS_ENABLED, isOn ? 1 : 0);
        return lastStatus;
    }

    /*
     * Disable the LockSound for Phoneloopback,return the last status
     */
    public static boolean setLockSound(Context context, boolean isOn) {
        boolean lastStatus = (Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.LOCKSCREEN_SOUNDS_ENABLED, 0) == 1);
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.LOCKSCREEN_SOUNDS_ENABLED, isOn ? 1 : 0);
        return lastStatus;
    }

    private static Map<String, Integer> mSupportMap = new HashMap<String, Integer>();

    /*
     * Some test item does support by PCBA.conf
     */
    public static boolean isSupportInPCBAConf(String itemName) {
        if (TextUtils.isEmpty(itemName)) {
            Log.w(TAG, "Empty itemName:" + itemName);
            return false;
        }
        Log.d(TAG, "containsKey containsKey=" + itemName);
        if (mSupportMap.containsKey(itemName)) {
            Integer support = mSupportMap.get(itemName);
            Log.d(TAG, "containsKey support=" + support);
            return support == 1;
        }
        return false;
    }

    public static void parsePCBAConf() {
        BufferedReader br = null;
        try {
            if (!FileUtils.fileIsExists(ENG_PCBA_SUPPORT_CONFIG)) {
                Log.w(TAG, "File not exists:" + ENG_PCBA_SUPPORT_CONFIG);
                return;
            }
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(ENG_PCBA_SUPPORT_CONFIG)),
                    "UTF-8"));
            String lineTxt = null;
            mSupportMap.clear();
            while ((lineTxt = br.readLine()) != null) {
                Log.d(TAG, "lineTxt=" + lineTxt);
                if (lineTxt.startsWith("#")) {
                    continue;
                }
                if (!TextUtils.isEmpty(lineTxt) && Character.isDigit(lineTxt.charAt(0))) {
                    String[] names = lineTxt.split("\t");
                    if (names.length == 3) {
                        String index = names[0];
                        String support = names[1];
                        String name = names[2];
                        Log.d(TAG, "index=" + index + ",support=" + support
                                + ",name=" + name);
                        Integer supportInt = 0;
                        try {
                            supportInt = Integer.valueOf(support);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "supportInt=" + supportInt);
                        mSupportMap.put(name, supportInt);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(br != null){
                    br.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static boolean isSupportAIKey() {
        if (!FileUtils.fileIsExists(KEY_INPUT_MODALIAS_PATH))
            return false;
        String result = FileUtils.readFile(KEY_INPUT_MODALIAS_PATH);
        Log.d(TAG, "isSupportAIKey result=" + result);
        if (!TextUtils.isEmpty(result)) {
            String[] strArray = result.split(",");
            for (String str : strArray) {
                if (AI_KEY_MODALIAS_VALUE.equalsIgnoreCase(str)) {
                    Log.d(TAG, "isSupportAIKey support AI key!");
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean isInteger(String str) {
        if(TextUtils.isEmpty(str)) return false;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str.trim()).matches();

    }
}
