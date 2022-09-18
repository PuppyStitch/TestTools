package com.sprd.validationtools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserManager;
import android.util.Log;

import com.sprd.validationtools.itemstest.ListItemTestActivity;

public class ValidationToolsBroadcastReceiver extends BroadcastReceiver {

    /* SPRD bug 843173:ZTE security code. */
    private static final String TAG = "ValidationToolsBroadcastReceiver";
    private static final String SECURITY_CODE = "654987";
    private static final String SECURITY_CODE_LIST_ITEM = "833";
    /* @} */
    private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String ACTION_LOCKED_BOOT_COMPLETED = "android.intent.action.LOCKED_BOOT_COMPLETED";
    private static final String BOOT_MODE = "ro.bootmode";
    private static final String BOOT_MODE_APKMMI = "apkmmi_mode";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive action = " + action);
        if(action == null) return;
        if(action.equals(ACTION_BOOT_COMPLETED)){
            String bootmode = SystemProperties.get(BOOT_MODE, "unknow");
            Log.d(TAG, "onReceive bootmode = " + bootmode);
            if(bootmode != null && bootmode.equals(BOOT_MODE_APKMMI)){
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setClass(context, ValidationToolsMainActivity.class);
                context.startActivity(i);
                return;
            }
        }
        Uri uri = intent.getData();
        if (uri == null)
            return;
        String host = uri.getHost();

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Log.d(TAG, "onReceive host = " + host);
        Log.d(TAG, "onReceive getAction = " + intent.getAction());
        if ("83789".equals(host)) {
            i.setClass(context, ValidationToolsMainActivity.class);
            context.startActivity(i);
        } else if (Const.isBoardISharkL210c10()) {
            if (SECURITY_CODE.equals(host)) {
                i.setClass(context, ValidationToolsMainActivity.class);
                context.startActivity(i);
            } else if (SECURITY_CODE_LIST_ITEM.equals(host)) {
                i.setClass(context, ListItemTestActivity.class);
                /* SPRD bug 855450:ZTE feature */
                i.putExtra(Const.SECURITY_CODE, SECURITY_CODE_LIST_ITEM);
                context.startActivity(i);
            }
        }
    }
}
