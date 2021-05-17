package com.peter.letsswtich.data.source

import com.peter.letsswtich.data.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO


class DefaultLetsSwitchRepository(
    private val remoteDataSource: LetsSwitchDataSource,
    private val localDataSource: LetsSwitchDataSource
) :
    LetsSwitchRepository {

    override suspend fun getUserItem(): List<User> {
        return remoteDataSource.getUserItem()
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

}