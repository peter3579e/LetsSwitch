package com.peter.letsswtich.map.eventDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.Events
import com.peter.letsswtich.data.source.LetsSwitchRepository

class EventDetailViewModel(private val letsSwitchRepository: LetsSwitchRepository, eventDetail:Events):ViewModel() {

    val event = eventDetail

    val _navigateBackToMap = MutableLiveData<Boolean>()
    val navigateBackToMap: LiveData<Boolean>
        get() = _navigateBackToMap

    fun navigateBackToMap(){
        _navigateBackToMap.value = true
    }

    fun mapNavigated(){
        _navigateBackToMap.value = false
    }
}