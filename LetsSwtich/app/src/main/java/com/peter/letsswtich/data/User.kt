package com.peter.letsswtich.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    var personImages: List<String> = listOf(),
    var description: String = "",
    var status: String ="",
    var id: String = "",
    var googleId: String ="",
    val age: Int = 0,
    var latitude: Double = 0.0,
    val lngti: Double = 0.0,
    var gender: String = "",
    var name: String = "",
    var email: String = "",
    var city: String = "",
    var district: String = "",
    var likeList: List<String> = listOf(),
    var role: String = "",
    var fluentLanguage: List<String> = listOf(),
    var preferLanguage: List<String> = listOf(),
    var backGroundPic: String = "",
    var matchTime: Long = 0L

        ): Parcelable