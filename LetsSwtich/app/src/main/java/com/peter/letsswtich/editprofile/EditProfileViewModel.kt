package com.peter.letsswtich.editprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository

class EditProfileViewModel(private val letsSwitchRepository: LetsSwitchRepository, userdetail:User):ViewModel() {

    val user = userdetail

    private val _navigateToProfile = MutableLiveData<Boolean>()

    val navigateToProfile: LiveData<Boolean>
        get() = _navigateToProfile


    fun navigateToProfile(){
        _navigateToProfile.value = true
    }

    fun profileNavigated(){
        _navigateToProfile.value = false
    }
}