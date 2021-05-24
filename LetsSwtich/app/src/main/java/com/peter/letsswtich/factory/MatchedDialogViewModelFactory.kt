package com.peter.letsswtich.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.chatroom.ChatRoomViewModel
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.dialog.MatchedDialogViewModel

class MatchedDialogViewModelFactory constructor(
        private val repository: LetsSwitchRepository,
        private val matchedUser: User
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(MatchedDialogViewModel::class.java) ->
                        MatchedDialogViewModel(repository, matchedUser)


                    else ->
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T
}