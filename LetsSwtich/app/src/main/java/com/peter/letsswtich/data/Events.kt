package com.peter.letsswtich.data

import android.os.Parcelable
import com.peter.letsswtich.login.UserManager
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Events(
        var eventId: String ="",
        var eventTitle: String = "",
        var eventDetail: String = "",
        var Location: Location = Location("",0.0,0.0),
        var date: String = "",
        var time: String = "",
        var peopleNumber: Int = 0,
        var eventPhotos: List<String> = listOf(),
        var postBy : String = "",
        var postTime: Long = 0L
) : Parcelable