package com.peter.letsswtich.data.source.local

import android.content.Context
import com.peter.letsswtich.data.*
import com.peter.letsswtich.data.source.LetsSwitchDataSource

class LetsSwitchLocalDataSource(val context: Context): LetsSwitchDataSource {
    override suspend fun getAllUser(): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatItem(): List<ChatRoom> {
        TODO("Not yet implemented")
    }

    override suspend fun getMessageItem(): List<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun getMapItem(): List<StoreLocation> {
        TODO("Not yet implemented")
    }

    override suspend fun postUser() {
        TODO("Not yet implemented")
    }

    override suspend fun updateAndCheckLike(myEmail: String, user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getLikeList(myEmail: String, user: User): Result<List<String>> {
        TODO("Not yet implemented")
    }
}