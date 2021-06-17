package com.peter.letsswtich.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.chatroom.ChatRoomViewModel
import com.peter.letsswtich.data.source.LetsSwitchRepository

class ChatRoomViewModelFactory constructor(
    private val repository: LetsSwitchRepository,
    private val userEmail: String,
    private val userName: String,
    private val fromMap: Boolean
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(ChatRoomViewModel::class.java) ->
                    ChatRoomViewModel(repository, userEmail, userName, fromMap)


                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}