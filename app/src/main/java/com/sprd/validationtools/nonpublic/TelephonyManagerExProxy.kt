package com.sprd.validationtools.nonpublic

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.CellLocation
import android.util.Log
import com.sprd.validationtools.appCtx

object TelephonyManagerExProxy {
    private const val TAG = "TELEPHONYMANAGEREXPROXY"
    private const val CLASS_NAME = "android.telephony.TelephonyManagerEx"
    private lateinit var tmExClass: Class<*>
    private lateinit var tmExObject: Any

    init {
        init()
    }

    @SuppressLint("PrivateApi")
    fun init() {
        try {
            tmExClass = Class.forName(CLASS_NAME)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "no such class, $CLASS_NAME")
            throw e
        }

        tmExObject = tmExClass.getMethod("from", Context::class.java).invoke(appCtx)
    }

    @JvmStatic
    fun getCellLocationForPhone(phoneId: Int): CellLocation {
        return tmExClass.getMethod("getCellLocationForPhone")
            .invoke(tmExObject, phoneId) as CellLocation
    }
}