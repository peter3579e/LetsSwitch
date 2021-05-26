package com.peter.letsswtich.data.source

import androidx.lifecycle.MutableLiveData
import com.peter.letsswtich.data.*

interface LetsSwitchDataSource {

    suspend fun getAllUser(): Result<List<User>>
    fun  getLiveChatList(myEmail: String): MutableLiveData<List<ChatRoom>>
    fun getAllLiveMessage(emails: List<String>): MutableLiveData<List<Message>>
    suspend fun getMessageItem(): List<Message>
    suspend fun getMapItem(): List<StoreLocation>
    suspend fun getLikeList(myEmail: String, user: User): Result<List<String>>
    fun getNewMatchListener(myEmail: String): MutableLiveData<List<User>>
    suspend fun getMyOldMatchList(myEmail: String): Result<List<User>>
    suspend fun postUser()
    suspend fun postMessage(emails: List<String>, message: Message): Result<Boolean>
    suspend fun postChatRoom(chatRoom: ChatRoom): Result<Boolean>
    suspend fun updateMyLike(myEmail: String, user: User): Result<Boolean>
    suspend fun updateMatch(myEmail: String, user: User): Result<Boolean>
    suspend fun removeUserFromLikeList(myEmail: String, user: User): Result<Boolean>


}