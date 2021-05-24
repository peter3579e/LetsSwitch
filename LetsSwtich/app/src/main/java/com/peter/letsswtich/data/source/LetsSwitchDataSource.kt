package com.peter.letsswtich.data.source

import androidx.lifecycle.MutableLiveData
import com.peter.letsswtich.data.*

interface LetsSwitchDataSource {

    suspend fun getAllUser(): Result<List<User>>
    suspend fun getChatItem(): List<ChatRoom>
    suspend fun getMessageItem(): List<Message>
    suspend fun getMapItem(): List<StoreLocation>
    suspend fun getLikeList(myEmail: String, user: User): Result<List<String>>
    fun getNewMatchListener(myEmail: String): MutableLiveData<List<User>>
    suspend fun getMyOldMatchList(myEmail: String): Result<List<User>>
    suspend fun postUser()
    suspend fun updateMyLike(myEmail: String, user: User): Result<Boolean>
    suspend fun updateMatch(myEmail: String, user: User): Result<Boolean>
    suspend fun removeUserFromLikeList(myEmail: String, user: User): Result<Boolean>


}