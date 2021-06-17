package com.peter.letsswtich.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageWithId(
    var message: List<Message> = listOf(Message("","","","","",0L,false)),
    var documentId: String
)

@Parcelize
data class Message(
    var id : String = "",
    var senderName : String = "",
    var senderImage : String = "",
    var senderEmail : String = "",
    var text : String = "",
    var createdTime : Long = 0L,
    var read: Boolean = false
):Parcelable