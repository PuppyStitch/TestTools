package com.sprd.validationtools.nonpublic

import android.annotation.SuppressLint

object SystemPropertiesProxy {

    private const val SYSPROP_CLASS_NAME = "android.os.SystemProperties"

    private lateinit var sysPropClass: Class<*>

    init {
        init()
    }

    @SuppressLint("PrivateApi")
    fun init() {
        sysPropClass = Class.forName(SYSPROP_CLASS_NAME)
    }

    @JvmStatic
    fun get(key: String): String {
        return sysPropClass.getMethod("get", String::class.java).invoke(null, key) as String
    }

    @JvmStatic
    fun get(key: String, defaultValue:String): String {
        return sysPropClass.getMethod("get", String::class.java, String::class.java)
            .invoke(null, key, defaultValue) as String
    }

    @JvmStatic
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sysPropClass.getMethod("getBoolean", String::class.java, Boolean::class.java)
            .invoke(null, key, defaultValue) as Boolean
    }

    @JvmStatic
    fun getInt(key: String, defaultValue: Int): Int {
        return sysPropClass.getMethod("getInt", String::class.java, Int::class.java)
            .invoke(null, key, defaultValue) as Int
    }

    @JvmStatic
    fun set(key: String, value: String) {
        sysPropClass.getMethod("set", String::class.java, String::class.java).invoke(null, key, value)
    }
}