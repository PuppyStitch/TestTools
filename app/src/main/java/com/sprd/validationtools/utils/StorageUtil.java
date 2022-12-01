package com.sprd.validationtools.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.sprd.validationtools.nonpublic.EnvironmentExProxy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorageUtil {
    public static final String TAG = "StorageUtil";

    private static final int EXT_EMULATED_PATH = 0;
    private static final int EXT_COMMON_PATH = 1;
    private static final int OTG_UDISK_PATH = 2;

    /*
     * type:0 --- External storage(SD card) emulated app directory.
     * type:1 --- External storage(SD card) common app directory.
     * type:2 --- USB mass storage(OTG U disk) app directory.
     */
    public static String getExternalStorageAppPath(Context context, int type) {
        String extEmulatedPath = null;
        String extCommonPath = null;
        String extOtgUdiskPath = null;
        String otgUdiskPath = "null";

        List<File> allDirPaths = new ArrayList<>();
        Collections.addAll(allDirPaths, context.getExternalFilesDirs(null));

        File[] otgPaths = EnvironmentExProxy.getUsbdiskVolumePaths();
        for (File file : otgPaths) {
            if (file != null && Environment.MEDIA_MOUNTED.equals(EnvironmentExProxy.getUsbdiskVolumeState(file))) {
                Log.d(TAG, "otg udisk mounted, otg path is " + file.getPath());
                otgUdiskPath = file.getPath();
            } else {
                Log.i(TAG, "otg udisk not mounted, otg path is null");
                otgUdiskPath = "null";
            }
        }

        for (File file : allDirPaths) {
            if (file != null) {
                String path = file.getAbsolutePath();
                if (path.startsWith("/storage/emulated/0")) {
                    Log.d(TAG, "external storage emulated path is: " + path);
                    extEmulatedPath = path;
                } else if (path.startsWith(otgUdiskPath)) {
                    Log.d(TAG, "external storage otg udisk path is: " + path);
                    extOtgUdiskPath = path;
                } else {
                    Log.d(TAG, "external storage common path is: " + path);
                    extCommonPath = path;
                }
            }
        }

        if (type == EXT_EMULATED_PATH) {
            return extEmulatedPath;
        } else if (type == EXT_COMMON_PATH) {
            return extCommonPath;
        } else if (type == OTG_UDISK_PATH) {
            return extOtgUdiskPath;
        } else {
            Log.w(TAG, "type is incorrect!");
            return null;
        }
    }

    public static String getExternalStoragePathState() {
        return EnvironmentExProxy.getExternalStoragePathState();
    }

    public static String getInternalStoragePath() {
        return EnvironmentExProxy.getInternalStoragePath().getAbsolutePath();
    }
}