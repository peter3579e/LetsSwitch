package com.peter.letsswtich.data.source

import com.peter.letsswtich.data.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO


class DefaultLetsSwitchRepository(
    private val remoteDataSource: LetsSwitchDataSource,
    private val localDataSource: LetsSwitchDataSource
) :
    LetsSwitchRepository {

    override suspend fun getAllUser(): Result<List<User>> {
        return remoteDataSource.getAllUser()
    }

    override suspend fun getChatItem(): List<ChatRoom> {
        return remoteDataSource.getChatItem()
    }

    override suspend fun getMessageItem(): List<Message> {
        return remoteDataSource.getMessageItem()
    }

    override suspend fun getMapItem() : List<StoreLocation> {
        return remoteDataSource.getMapItem()
    }

    override suspend fun postUser(){
        return remoteDataSource.postUser()
    }

    override suspend fun updateAndCheckLike(myEmail: String, user: User): Result<Boolean>{
        return remoteDataSource.updateAndCheckLike(myEmail,user)
    }

}