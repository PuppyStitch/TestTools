package com.sprd.validationtools.utils;

import android.util.Log;

public class Native {

    static {
        try {
            System.loadLibrary("jni_validationtools");
        } catch (UnsatisfiedLinkError e) {
            Log.d("ValidationToolsNative", " #loadLibrary jni_validationtools failed  ");
            e.printStackTrace();
        }
    }

    /**
     * send AT cmd to modem
     *
     * @return (String: the return value of send cmd, "OK":sucess, "ERROR":fail)
     */
    public static native String native_sendATCmd(int phoneId, String cmd);

    public static native int native_get_audio_whale_loopback_flag();

    public static native int native_get_camera_sensor_tof_support_flag();

    public static native int native_get_TARGET_CAMERA_SENSOR_CCT_TCS3430_flag();
    /**
    *
    *
    * @return (int: 1:support, other:not support)
    */
   public static native int native_is_support_macro(String macroName);
}