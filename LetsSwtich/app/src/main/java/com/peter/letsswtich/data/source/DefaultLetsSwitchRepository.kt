package com.peter.letsswtich.data.source

import androidx.lifecycle.MutableLiveData
import com.peter.letsswtich.data.*


class DefaultLetsSwitchRepository(
    private val remoteDataSource: LetsSwitchDataSource,
    private val localDataSource: LetsSwitchDataSource
) :
    LetsSwitchRepository {

    override suspend fun getAllUser(): Result<List<User>> {
        return remoteDataSource.getAllUser()
    }

    override fun getLiveChatList(myEmail: String): MutableLiveData<List<ChatRoom>> {
        return remoteDataSource.getLiveChatList(myEmail)
    }

    override fun getAllLiveMessage(emails: List<String>): MutableLiveData<List<Message>> {
        return remoteDataSource.getAllLiveMessage(emails)
    }


    override suspend fun getMessageItem(): List<Message> {
        return remoteDataSource.getMessageItem()
    }

    override suspend fun getMapItem() : List<StoreLocation> {
        return remoteDataSource.getMapItem()
    }

    override suspend fun getLikeList(myEmail: String, user: User): Result<List<String>> {
        return remoteDataSource.getLikeList(myEmail,user)
    }

    override fun getNewMatchListener(myEmail: String): MutableLiveData<List<User>> {
        return remoteDataSource.getNewMatchListener(myEmail)
    }

    override suspend fun getMyOldMatchList(myEmail: String): Result<List<User>> {
        return remoteDataSource.getMyOldMatchList(myEmail)
    }

    override suspend fun postMessage(emails: List<String>, message: Message): Result<Boolean> {
        return remoteDataSource.postMessage(emails, message)
    }

    override suspend fun postUser(){
        return remoteDataSource.postUser()
    }

    override suspend fun postChatRoom(chatRoom: ChatRoom): Result<Boolean> {
        return remoteDataSource.postChatRoom(chatRoom)
    }

    override suspend fun updateMyLike(myEmail: String, user: User): Result<Boolean>{
        return remoteDataSource.updateMyLike(myEmail,user)
    }

    override suspend fun updateMatch(myEmail: String, user: User): Result<Boolean> {
        return remoteDataSource.updateMatch(myEmail,user)}

    override suspend fun removeUserFromLikeList(myEmail: String, user: User): Result<Boolean>{
        return remoteDataSource.removeUserFromLikeList(myEmail,user)
    }

}