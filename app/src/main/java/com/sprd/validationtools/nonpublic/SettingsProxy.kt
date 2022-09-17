package com.sprd.validationtools.nonpublic

import android.provider.Settings

object SettingsProxy {

    object Global {
        @JvmField
        val PREFERRED_NETWORK_MODE = getFieldValue("PREFERRED_NETWORK_MODE")

        @JvmField
        val MULTI_SIM_DATA_CALL_SUBSCRIPTION = getFieldValue("MULTI_SIM_DATA_CALL_SUBSCRIPTION")

        @JvmField
        val BLE_SCAN_ALWAYS_AVAILABLE = getFieldValue("BLE_SCAN_ALWAYS_AVAILABLE")


        private fun getFieldValue(fieldName: String): String {
            return Settings::class.java.getField(fieldName).get(null) as String
        }
    }
}