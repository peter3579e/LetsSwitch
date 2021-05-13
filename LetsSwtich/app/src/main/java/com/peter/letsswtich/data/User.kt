package com.peter.letsswtich.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    var id: Int,
    var name: String = "",
    var email: String = "",
    var profilePic: Array<String>,
    var bigheadPic: String,
    var city: String = "",
    var district: String = "",
    var gender: String = "",
    var description: String = "",
    var role: String
//    var experience: String = "",
//    var joinedTime: Long = 0,
//    var followingEmail: List<String> = listOf(),
//    var followingName: List<String> = listOf(),
//    var followedBy: List<String> = listOf()
        ): Parcelable