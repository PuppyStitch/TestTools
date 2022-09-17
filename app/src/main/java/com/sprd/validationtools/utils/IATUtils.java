package com.sprd.validationtools.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.annotation.SuppressLint;
import android.util.Log;

public class IATUtils {
    private static final String TAG = "IATUtils";
    public static String AT_FAIL = "AT FAILED";
    public static String AT_OK = "OK";
    public static int mPhoneCount = 0;

    public static String AT_CONNECT = "CONNECT";
    public static String AT_NOT_SUPPORT = "ERROR: 4";

    private static String HIDL_SOCKET_NAME = "hidl_common_socket";
    private static String AT_SOCKET_NAME = "miscserver";

    /*
     * public static String sendATCmd(String cmd, String serverName) { try {
     * String result = sendAtCmd(cmd); return result; }catch (Exception e){
     * Log.d("AT Exception",e.toString()); } return "error service can't get"; }
     * 
     * public static String sendAtCmd(String cmd){ String result =
     * Native.native_sendATCmd(0, cmd); if(!result.contains("OK")){ result =
     * IATUtils.AT_FAIL; } Log.d(TAG, "result = " + result); return result; }
     */

    public static String sendAt(final String cmd, final String serverName) {
        //here we fix AT blocking time, we return AT fail after 2 seconds later.
        final ExecutorService exec = Executors.newFixedThreadPool(1);
        String futureObj;
        try {
            Future<String> future = exec.submit(() -> sendATCmd(cmd, serverName));
            futureObj = future.get(2000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            Log.d(TAG, "modem at timeout", ex);
            return IATUtils.AT_FAIL;
        } catch (Exception e) {
            Log.d(TAG, "modem at exception", e);
            return IATUtils.AT_FAIL;
        }
        exec.shutdown();
        return futureObj != null ? futureObj : IATUtils.AT_FAIL;
    }

    private static String getChannelBySim(int simIdx) {
        return "atchannel" + simIdx;
    }

    private static int getPhoneIdByChannelName(String channelName) {
        if (channelName.contains("atchannel0")) {
            return 0;
        } else if (channelName.contains("atchannel1")) {
            return 1;
        } else {
            return 0;
        }
    }

    @SuppressLint("DefaultLocale")
    private static String getAtCmd(String cmd, int phoneId) {
        return String.format("%s sendAt %d %s", AT_SOCKET_NAME, phoneId, cmd);
    }

    public static synchronized String sendATCmd(String cmd, int phoneId) {
        return SocketUtils.sendCmd(HIDL_SOCKET_NAME, getAtCmd(cmd, phoneId));
    }

    public static synchronized String sendATCmd(String cmd, String channelName) {
        return sendATCmd(cmd, getPhoneIdByChannelName(channelName));
    }

    public static String sendAtCmd(String cmd) {
        String result = sendATCmd(cmd, "atchannel0");
        if (result == null || !result.contains("OK")) {
            result = IATUtils.AT_FAIL;
        }
        Log.d(TAG, "result = " + result);
        return result;
    }
}
