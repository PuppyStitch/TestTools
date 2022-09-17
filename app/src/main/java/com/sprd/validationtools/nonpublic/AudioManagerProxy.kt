package com.sprd.validationtools.nonpublic

import android.content.Context
import android.media.AudioManager
import com.sprd.validationtools.appCtx

object AudioManagerProxy {
    @JvmField
    val EXTRA_VOLUME_STREAM_TYPE: String = getField("EXTRA_VOLUME_STREAM_TYPE") as String

    @JvmField
    val EXTRA_VOLUME_STREAM_VALUE: String = getField("EXTRA_VOLUME_STREAM_VALUE") as String

    @JvmField
    val DEVICE_OUT_FM_HEADSET: Int = getField("DEVICE_OUT_FM_HEADSET") as Int

    @JvmField
    val VOLUME_CHANGED_ACTION: String = getField("VOLUME_CHANGED_ACTION") as String

    private var audioManager: AudioManager =
        appCtx.getSystemService(Context.AUDIO_SERVICE) as AudioManager


    private fun getField(fieldName: String): Any {
        return AudioManager::class.java.getField(fieldName).get(null)!!
    }

    @JvmStatic
    fun setParameters(keyValuePairs: String) {
        AudioManager::class.java.getMethod("setParameters", String::class.java).invoke(audioManager, keyValuePairs)
    }
}