package com.peter.letsswtich.util

import android.util.Log
import com.peter.letsswtich.BuildConfig

object Logger {

    private const val TAG = "Peter-LetsSwitch"

    fun v(content: String) {
        if (BuildConfig.LOGGER_VISIABLE) Log.v(TAG, content)
    }

    fun d(content: String) {
        if (BuildConfig.LOGGER_VISIABLE) Log.d(TAG, content)
    }

    fun i(content: String) {
        if (BuildConfig.LOGGER_VISIABLE) Log.i(TAG, content)
    }

    fun w(content: String) {
        if (BuildConfig.LOGGER_VISIABLE) Log.w(TAG, content)
    }

    fun e(content: String) {
        if (BuildConfig.LOGGER_VISIABLE) Log.e(TAG, content)
    }

}