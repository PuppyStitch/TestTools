package com.sprd.validationtools.nonpublic

import android.annotation.SuppressLint

object AudioSystemProxy {

    private const val AS_CLASS_NAME = "android.media.AudioSystem"
    private lateinit var asClass: Class<*>

    init {
        init()
    }

    @SuppressLint("PrivateApi")
    fun init() {
        asClass = Class.forName(AS_CLASS_NAME)
    }

    @JvmField
    val DEVICE_STATE_UNAVAILABLE: Int = asClass.getField("DEVICE_STATE_UNAVAILABLE").get(null) as Int

    @JvmField
    val DEVICE_STATE_AVAILABLE: Int = asClass.getField("DEVICE_STATE_AVAILABLE").get(null) as Int

    @JvmField
    val FOR_FM: Int = asClass.getField("FOR_FM").get(null) as Int

    @JvmField
    val FORCE_SPEAKER: Int = asClass.getField("FORCE_SPEAKER").get(null) as Int

    @JvmField
    val FORCE_NONE: Int = asClass.getField("FORCE_NONE").get(null) as Int

    @JvmStatic
    fun setDeviceConnectionState(device: Int, state: Int, device_address: String, device_name: String): Int {
        return asClass. getMethod("setDeviceConnectionState",
            Int::class.java, Int::class.java, String::class.java, String::class.java)
            .invoke(null, device, state, device_address, device_name) as Int
    }

    @JvmStatic
    fun setForceUse(usage: Int, config: Int): Int {
        return asClass. getMethod("setForceUse", Int::class.java, Int::class.java)
            .invoke(null, usage, config) as Int
    }
}