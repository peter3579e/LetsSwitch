package com.peter.letsswtich.data.source

import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO



class DefaultLetsSwitchRepository(private val remoteDataSource: LetsSwitchDataSource,
                                  private val localDataSource: LetsSwitchDataSource) :
    LetsSwitchRepository {

        override suspend fun getUserItem(): List<User>{
            return remoteDataSource.getUserItem()
        }

}