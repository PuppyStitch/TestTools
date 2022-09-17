package com.sprd.validationtools.nonpublic

import android.annotation.SuppressLint
import android.content.Context

object ImsManagerProxy {

    private const val IMSMANAGER_CLASS_NAME = "com.android.ims.ImsManager"

    private lateinit var imsManagerClass: Class<*>

    init {
        init()
    }

    @SuppressLint("PrivateApi")
    fun init() {
        imsManagerClass = Class.forName(IMSMANAGER_CLASS_NAME)
    }

    fun setEnhanced4gLteModeSetting(ctx: Context, enable: Boolean) {
        imsManagerClass.getMethod("setEnhanced4gLteModeSetting", Context::class.java, Boolean::class.java)
            .invoke(null, ctx, enable)
    }

    fun isWfcEnabledByPlatform(ctx: Context): Boolean {
        return imsManagerClass.getMethod("isWfcEnabledByPlatform", Context::class.java)
            .invoke(null, ctx) as Boolean
    }
}
