package com.peter.letsswtich.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.chatroom.ChatRoomViewModel
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.profile.PicViewModel

class PicViewModelFactory constructor(
    private val repository: LetsSwitchRepository,
    private val userImage: String

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(PicViewModel::class.java) ->
                    PicViewModel(repository, userImage)


                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}