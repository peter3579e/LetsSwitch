package com.peter.letsswtich.data.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.peter.letsswtich.data.*
import com.peter.letsswtich.data.source.LetsSwitchDataSource

class LetsSwitchLocalDataSource(val context: Context): LetsSwitchDataSource {
    override suspend fun getAllUser(): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override fun getLiveChatList(myEmail: String): MutableLiveData<List<ChatRoom>> {
        TODO("Not yet implemented")
    }

    override fun getAllLiveMessage(emails: List<String>): MutableLiveData<MessageWithId> {
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

    override suspend fun postMessage(emails: List<String>, message: Message): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun postChatRoom(chatRoom: ChatRoom): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateIsRead(friendEmail: String, documentId: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMyLike(myEmail: String, user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getLikeList(myEmail: String, user: User): Result<List<String>> {
        TODO("Not yet implemented")
    }

    override fun getNewMatchListener(myEmail: String): MutableLiveData<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetail(userEmail: String): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyOldMatchList(myEmail: String): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMatch(myEmail: String, user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeUserFromLikeList(myEmail: String, user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }
}