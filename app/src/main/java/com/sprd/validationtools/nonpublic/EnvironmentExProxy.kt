package com.sprd.validationtools.nonpublic

import android.annotation.SuppressLint
import java.io.File

object EnvironmentExProxy {

    private const val ENVEX_CLASS_NAME = "android.os.EnvironmentEx"

    private lateinit var environmentExClass: Class<*>

    init {
        init()
    }

    @SuppressLint("PrivateApi")
    fun init() {
        environmentExClass = Class.forName(ENVEX_CLASS_NAME)
    }

    @JvmStatic
    fun getInternalStoragePath(): File {
        return environmentExClass.getMethod("getInternalStoragePath").invoke(null) as File
    }

    @JvmStatic
    fun getExternalStoragePathState(): String {
        return environmentExClass.getMethod("getExternalStoragePathState").invoke(null) as String
    }

    @JvmStatic
    fun getUsbdiskVolumePaths(): Array<File>? {
        return environmentExClass.getMethod("getUsbdiskVolumePaths").invoke(null) as Array<File>?
    }

    @JvmStatic
    fun getUsbdiskVolumeState(file: File): String {
        return environmentExClass.getMethod("getUsbdiskVolumeState",File::class.java).invoke(null,file) as String
    }
}