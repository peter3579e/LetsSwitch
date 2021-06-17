package com.peter.letsswtich.util

import com.peter.letsswtich.LetsSwtichApplication

object Util {

    fun getColor(resourceId: Int): Int {
        return LetsSwtichApplication.instance.getColor(resourceId)
    }

}