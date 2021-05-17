package com.peter.letsswtich.data.source

import com.peter.letsswtich.data.*

interface LetsSwitchDataSource {

    suspend fun getUserItem(): List<User>
    suspend fun getChatItem(): List<ChatRoom>
    suspend fun getMessageItem(): List<Message>
    suspend fun getMapItem(): List<StoreLocation>

}