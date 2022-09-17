package com.sprd.validationtools

import android.annotation.SuppressLint
import android.content.Context

/**
 *  It's OK here to keep the static context because we only keep application context
 */
@SuppressLint("StaticFieldLeak")
lateinit var appCtx: Context