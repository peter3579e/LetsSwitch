package com.peter.letsswtich.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Requirement(
        var gender: String = "",
        var language: String = "",
        var age: List<Int> = listOf(),
        var city: String = ""
) : Parcelable