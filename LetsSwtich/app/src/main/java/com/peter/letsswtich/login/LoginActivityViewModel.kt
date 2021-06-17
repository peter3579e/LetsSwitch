package com.peter.letsswtich.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.util.CurrentFragmentType

class LoginActivityViewModel(private val letsSwitchRepository: LetsSwitchRepository):ViewModel() {
    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()
}