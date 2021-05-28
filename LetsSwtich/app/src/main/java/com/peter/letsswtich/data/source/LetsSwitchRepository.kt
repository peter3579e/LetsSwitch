package com.peter.letsswtich.data.source

import androidx.lifecycle.MutableLiveData
import com.peter.letsswtich.data.*

interface LetsSwitchRepository {
    suspend fun getAllUser(): Result<List<User>>
    fun getLiveChatList(myEmail: String):  MutableLiveData<List<ChatRoom>>
    fun getAllLiveMessage(emails: List<String>): MutableLiveData<MessageWithId>
    suspend fun getMessageItem(): List<Message>
    suspend fun getMapItem(): List<StoreLocation>
    fun getNewMatchListener(myEmail: String): MutableLiveData<List<User>>
    suspend fun getLikeList(myEmail: String, user: User): Result<List<String>>
    suspend fun getMyOldMatchList(myEmail: String): Result<List<User>>
    suspend fun getUserDetail(userEmail:String): Result<User>
    suspend fun postUser()
    suspend fun updateIsRead(friendEmail:String,documentId: String):Result<Boolean>
    suspend fun postMessage(emails: List<String>, message: Message): Result<Boolean>
    suspend fun postChatRoom(chatRoom: ChatRoom): Result<Boolean>
    suspend fun updateMyLike(myEmail: String, user: User): Result<Boolean>
    suspend fun updateMatch(myEmail: String, user: User): Result<Boolean>
    suspend fun removeUserFromLikeList(myEmail: String, user: User): Result<Boolean>
}