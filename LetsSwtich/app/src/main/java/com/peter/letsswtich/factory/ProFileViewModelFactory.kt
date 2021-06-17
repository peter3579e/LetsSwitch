package com.peter.letsswtich.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.dialog.MatchedDialogViewModel
import com.peter.letsswtich.editprofile.EditProfileViewModel
import com.peter.letsswtich.profile.ProfileViewModel

class ProFileViewModelFactory constructor(
    private val repository: LetsSwitchRepository,
    private val matchedUser: User,
    private val fromMap: Boolean
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(repository, matchedUser, fromMap)


                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}