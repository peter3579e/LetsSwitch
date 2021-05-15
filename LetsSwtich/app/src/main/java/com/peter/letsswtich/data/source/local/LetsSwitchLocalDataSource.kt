package com.peter.letsswtich.data.source.local

import android.content.Context
import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchDataSource

class LetsSwitchLocalDataSource(val context: Context): LetsSwitchDataSource {
    override suspend fun getUserItem(): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatItem(): List<ChatRoom> {
        TODO("Not yet implemented")
    }
}