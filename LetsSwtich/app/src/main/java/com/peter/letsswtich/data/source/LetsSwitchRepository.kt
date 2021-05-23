package com.peter.letsswtich.data.source

import com.peter.letsswtich.data.*

interface LetsSwitchRepository {
    suspend fun getAllUser(): Result<List<User>>
    suspend fun getChatItem(): List<ChatRoom>
    suspend fun getMessageItem(): List<Message>
    suspend fun getMapItem(): List<StoreLocation>
    suspend fun postUser()
    suspend fun updateAndCheckLike(myEmail: String, user: User): Result<Boolean>
    suspend fun getLikeList(myEmail: String, user: User): Result<List<String>>
}