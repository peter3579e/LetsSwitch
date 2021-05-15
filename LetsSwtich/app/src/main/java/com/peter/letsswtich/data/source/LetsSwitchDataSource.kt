package com.peter.letsswtich.data.source

import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User

interface LetsSwitchDataSource {

    suspend fun getUserItem(): List<User>
    suspend fun getChatItem(): List<ChatRoom>
}