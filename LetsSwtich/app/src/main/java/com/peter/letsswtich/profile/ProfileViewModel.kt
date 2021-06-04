package com.peter.letsswtich.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.component.GridSpacingItemDecoration
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.network.LoadApiStatus

class ProfileViewModel(letsSwitchRepository: LetsSwitchRepository, user: com.peter.letsswtich.data.User):ViewModel() {

    val userDetail = user

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _navigateToEditProfile = MutableLiveData<Boolean>()

    val navigateToEditProfile: LiveData<Boolean>
        get() = _navigateToEditProfile

    fun navigateToEditProfile(){
        _navigateToEditProfile.value = true
    }

    fun editProfileNavigated(){
        _navigateToEditProfile.value = false
    }

    val decoration = GridSpacingItemDecoration(
            4,
            LetsSwtichApplication.instance.resources.getDimensionPixelSize(R.dimen.space_photo_grid), true
    )


}