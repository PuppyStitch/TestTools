package com.sprd.validationtools.nonpublic

import android.annotation.SuppressLint
import android.os.RemoteException

object ThermalProxy {

    private const val THERMAL_CLASS_NAME = "vendor.sprd.hardware.thermal.V1_0.IExtThermal"

    private var thermalObject: Class<*>? = null

    init {
        init()
    }

    @SuppressLint("PrivateApi")
    fun init() {
        try {
            thermalObject = Class.forName(THERMAL_CLASS_NAME)
        } catch (e: Exception) {
        }

    }

    @JvmStatic
    fun exists(): Boolean {
        return thermalObject != null
    }

    @JvmStatic
    @Throws(RemoteException::class)
    fun getService(): Any? {
        return thermalObject?.getMethod("getService")?.invoke(null)
    }

    @JvmStatic
    @Throws(RemoteException::class)
    fun setExtThermal(var1: Int) {
        thermalObject?.getMethod("setExtThermal", Int::class.java)?.invoke(getService(), var1)
    }

    @JvmStatic
    @Throws(RemoteException::class)
    fun getExtThermal(var1: Int): Boolean {
        return thermalObject?.getMethod("getExtThermal", Int::class.java)
            ?.invoke(getService(), var1) as Boolean
    }
}

object Constants {
    object ExtThermalCmd {
        private const val THERMAL_CONST_CLASS_NAME = "vendor.sprd.hardware.thermal.V1_0.Constants\$ExtThermalCmd"
        private var thermalConstObject: Class<*>? = null

        init {
            try {
                thermalConstObject = Class.forName(THERMAL_CONST_CLASS_NAME)
            } catch (e: Exception) {
            }
        }

        @JvmField
        val THMCMD_SET_EN = getFieldValue("THMCMD_SET_EN")
        @JvmField
        val THMCMD_SET_DIS = getFieldValue("THMCMD_SET_DIS")
        @JvmField
        val THMCMD_GET_STAT = getFieldValue("THMCMD_GET_STAT")
        @JvmField
        val THMCMD_SET_PA_EN = getFieldValue("THMCMD_SET_PA_EN")
        @JvmField
        val THMCMD_SET_PA_DIS = getFieldValue("THMCMD_SET_PA_DIS")
        @JvmField
        val THMCMD_GET_PA_STAT = getFieldValue("THMCMD_GET_PA_STAT")
        @JvmField
        val THMCMD_SET_CHG_EN = getFieldValue("THMCMD_SET_CHG_EN")
        @JvmField
        val THMCMD_SET_CHG_DIS = getFieldValue("THMCMD_SET_CHG_DIS")
        @JvmField
        val THMCMD_GET_CHG_STAT = getFieldValue("THMCMD_GET_CHG_STAT")

        private fun getFieldValue(fieldName: String): Int {
            return thermalConstObject?.getField(fieldName)?.get(null) as Int
        }
    }
}
