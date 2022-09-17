package com.sprd.validationtools.nonpublic

import android.annotation.SuppressLint


object TelephonyPropertiesProxy {
    private const val TP_CLASS_NAME = "com.android.internal.telephony.TelephonyProperties"
    private lateinit var tpClass: Class<*>

    init {
        init()
    }

    @SuppressLint("PrivateApi")
    fun init() {
        tpClass = Class.forName(TP_CLASS_NAME)
    }

    @JvmField
    val PROPERTY_BASEBAND_VERSION: String = tpClass.getField("PROPERTY_BASEBAND_VERSION").get(null) as String

    @JvmField
    val PROPERTY_ICC_OPERATOR_NUMERIC: String = tpClass.getField("PROPERTY_ICC_OPERATOR_NUMERIC").get(null) as String


}