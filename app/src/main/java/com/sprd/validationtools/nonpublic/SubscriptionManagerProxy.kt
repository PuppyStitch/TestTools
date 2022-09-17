package com.sprd.validationtools.nonpublic

import android.annotation.SuppressLint
import android.net.Uri
import android.content.Context
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import com.sprd.validationtools.appCtx

object SubscriptionManagerProxy {
    @JvmField
    val SIM_SLOT_INDEX: String =
        SubscriptionManager::class.java.getField("SIM_SLOT_INDEX").get(null) as String

    @JvmField
    val INVALID_SIM_SLOT_INDEX: Int =
        SubscriptionManager::class.java.getField("INVALID_SIM_SLOT_INDEX").get(null) as Int

    @JvmField
    val CONTENT_URI: Uri =
        SubscriptionManager::class.java.getField("CONTENT_URI").get(null) as Uri

    private val subscriptionManagerObject: SubscriptionManager =
        appCtx.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

    @JvmStatic
    fun getDefaultDataPhoneId(): Int {
        return SubscriptionManager::class.java.getMethod("getDefaultDataPhoneId")
            .invoke(subscriptionManagerObject) as Int
    }

    @JvmStatic
    fun getSubId(slotIdx: Int): IntArray? {
        return SubscriptionManager::class.java.getMethod("getSubId", Int::class.java)
            .invoke(null, slotIdx) as IntArray?
    }

    @JvmStatic
    fun isValidPhoneId(slotIdx: Int): Boolean {
        return SubscriptionManager::class.java.getMethod("isValidPhoneId", Int::class.java)
            .invoke(null, slotIdx) as Boolean
    }

    @JvmStatic
    fun isValidSubscriptionId(subId: Int): Boolean {
        return SubscriptionManager::class.java.getMethod("isValidSubscriptionId", Int::class.java)
            .invoke(null, subId) as Boolean
    }

    @JvmStatic
    fun getActiveSubscriptionInfoForSimSlotIndex(slotIndex: Int): SubscriptionInfo? {
        return SubscriptionManager::class.java.getMethod("getActiveSubscriptionInfoForSimSlotIndex", Int::class.java)
            .invoke(subscriptionManagerObject, slotIndex) as SubscriptionInfo
    }

    @JvmStatic
    fun getActiveSubscriptionIdList(): IntArray {
        return SubscriptionManager::class.java.getMethod("getActiveSubscriptionIdList")
            .invoke(subscriptionManagerObject) as IntArray
    }


    @SuppressLint("MissingPermission")
    @JvmStatic
    fun getActiveSubscriptionInfo(subId: Int): SubscriptionInfo {
        return subscriptionManagerObject.getActiveSubscriptionInfo(subId)
    }

}
