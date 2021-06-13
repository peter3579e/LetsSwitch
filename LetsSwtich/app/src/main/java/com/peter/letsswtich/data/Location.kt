package com.peter.letsswtich.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Location(
        var placeName: String = "",
        var latitude : Double = 0.0,
        var lngti: Double = 0.0,
        var address: String = ""
) : Parcelable