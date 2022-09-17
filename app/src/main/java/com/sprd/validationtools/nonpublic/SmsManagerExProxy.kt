package com.sprd.validationtools.nonpublic

import android.annotation.SuppressLint

object SmsManagerExProxy {

    private const val SMSMANAGEREX_CLASS_NAME = "android.telephony.SmsManagerEx"

    private lateinit var smsManagerExClass: Class<*>
    private lateinit var smsManagerExObject: Any

    init {
        init()
    }

    @SuppressLint("PrivateApi")
    fun init() {
        smsManagerExClass = Class.forName(SMSMANAGEREX_CLASS_NAME)
        smsManagerExObject = smsManagerExClass.getMethod("getDefault").invoke(null)!!
    }

    fun getSmscForSubscriber(subId: Int): String {
        return smsManagerExClass.getMethod("getSmscForSubscriber", Int::class.java)
            .invoke(smsManagerExObject, subId) as String
    }

    fun setSmscForSubscriber(subId: Int, smsc: String): Boolean {
        return smsManagerExClass.getMethod("setSmscForSubscriber", Int::class.java, String::class.java)
            .invoke(smsManagerExObject, subId, smsc) as Boolean
    }
}