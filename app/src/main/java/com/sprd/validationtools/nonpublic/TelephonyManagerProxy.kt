package com.sprd.validationtools.nonpublic

import android.content.Context
import android.telephony.ServiceState
import android.telephony.TelephonyManager

import com.sprd.validationtools.appCtx

object TelephonyManagerProxy {
    @JvmField
    val NETWORK_TYPE_LTE_CA: Int =
        TelephonyManager::class.java.getField("NETWORK_TYPE_LTE_CA").get(null) as Int

//    @JvmField
//    val NETWORK_TYPE_LTE_CA: Int = 19

    private var telephonyManagerObject: TelephonyManager =
            appCtx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    fun getTelephonyManager(): TelephonyManager {
        return telephonyManagerObject
    }

    @JvmStatic
    fun getService(): TelephonyManager {
       return telephonyManagerObject
    }


    @JvmStatic
    fun getPhoneCount(): Int {
        return telephonyManagerObject.phoneCount
    }

    @JvmStatic
    fun getSimState(simIdx: Int): Int {
        return telephonyManagerObject.getSimState(simIdx)
    }

    @JvmStatic
    fun getPreferredNetworkType(subId: Int): Int {
        return TelephonyManager::class.java.getMethod("getPreferredNetworkType", Int::class.java)
            .invoke(telephonyManagerObject, subId) as Int
    }

    @JvmStatic
    fun setNetworkRoaming(isRoaming: Boolean) {
        TelephonyManager::class.java.getMethod("setNetworkRoaming", Boolean::class.java)
            .invoke(telephonyManagerObject, isRoaming) as Int
    }

    @JvmStatic
    fun isSimExist(simIndex: Int): Boolean {
        return getSimState(simIndex) == TelephonyManager.SIM_STATE_READY
    }

    @JvmStatic
    fun getNetworkType(subId: Int): Int {
        return TelephonyManager::class.java.getMethod("getNetworkType", Int::class.java)
            .invoke(telephonyManagerObject, subId) as Int
    }

    @JvmStatic
    fun getNetworkTypeName(type: Int): String {
        return TelephonyManager::class.java.getMethod("getNetworkTypeName", Int::class.java)
            .invoke(telephonyManagerObject, type) as String
    }

    @JvmStatic
    fun getVoiceNetworkType(subId: Int): Int {
        return TelephonyManager::class.java.getMethod("getVoiceNetworkType", Int::class.java)
            .invoke(telephonyManagerObject, subId) as Int
    }

    @JvmStatic
    fun getNetworkOperatorName(subId: Int): String {
        return TelephonyManager::class.java.getMethod("getNetworkOperatorName", Int::class.java)
            .invoke(telephonyManagerObject, subId) as String
    }

    @JvmStatic
    fun getNetworkOperatorForPhone(phoneId: Int): String {
        return TelephonyManager::class.java.getMethod("getNetworkOperatorForPhone", Int::class.java)
            .invoke(telephonyManagerObject, phoneId) as String
    }

    @JvmStatic
    fun isNetworkRoaming(subId: Int): Boolean {
        return TelephonyManager::class.java.getMethod("isNetworkRoaming", Int::class.java)
            .invoke(telephonyManagerObject, subId) as Boolean
    }

    @JvmStatic
    fun hasIccCard(slotIndex: Int): Boolean {
        return TelephonyManager::class.java.getMethod("hasIccCard", Int::class.java)
            .invoke(telephonyManagerObject, slotIndex) as Boolean
    }

    @JvmStatic
    fun isMultiSimEnabled(): Boolean {
        return TelephonyManager::class.java.getMethod("isMultiSimEnabled")
            .invoke(telephonyManagerObject) as Boolean
    }

    @JvmStatic
    fun getCurrentPhoneTypeForSlot(slotIndex: Int): Int {
        return TelephonyManager::class.java.getMethod("getCurrentPhoneTypeForSlot", Int::class.java)
            .invoke(telephonyManagerObject, slotIndex) as Int
    }

    @JvmStatic
    fun getSubscriberId(subId: Int): String {
        return TelephonyManager::class.java.getMethod("getSubscriberId", Int::class.java)
            .invoke(telephonyManagerObject, subId) as String
    }

    @JvmStatic
    fun getSimOperatorNumericForPhone(phoneId: Int): String {
        return TelephonyManager::class.java.getMethod("getSimOperatorNumericForPhone", Int::class.java)
            .invoke(telephonyManagerObject, phoneId) as String
    }

    @JvmStatic
    fun getCurrentPhoneType(subId: Int): Int {
        return TelephonyManager::class.java.getMethod("getCurrentPhoneType", Int::class.java)
            .invoke(telephonyManagerObject, subId) as Int
    }

    @JvmStatic
    fun  getServiceStateForSubscriber(subId: Int): ServiceState {
        return TelephonyManager::class.java.getMethod("getServiceStateForSubscriber", Int::class.java)
            .invoke(telephonyManagerObject, subId) as ServiceState
    }

    @JvmStatic
    fun getCdmaPrlVersion(subId: Int): String {
        return TelephonyManager::class.java.getMethod("getCdmaPrlVersion", Int::class.java)
            .invoke(telephonyManagerObject, subId) as String
    }

    @JvmStatic
    fun getEsn(): String {
        return TelephonyManager::class.java.getMethod("getEsn")
            .invoke(telephonyManagerObject) as String
    }

}
