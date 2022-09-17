package com.sprd.validationtools.nonpublic

import android.telephony.ServiceState

object ServiceStateProxy {

    @JvmStatic
    fun getDataNetworkType(ss: ServiceState): Int {
        return ServiceState::class.java.getMethod("getDataNetworkType").invoke(ss) as Int
    }

    @JvmStatic
    fun getVoiceRegState(ss: ServiceState): Int {
        return ServiceState::class.java.getMethod("getVoiceRegState").invoke(ss) as Int
    }
}