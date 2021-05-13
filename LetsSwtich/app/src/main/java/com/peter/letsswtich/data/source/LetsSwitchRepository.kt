package com.peter.letsswtich.data.source

import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User

interface LetsSwitchRepository {
    suspend fun getUserItem(): List<User>
}