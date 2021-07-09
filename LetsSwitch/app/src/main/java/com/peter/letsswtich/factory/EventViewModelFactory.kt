package com.peter.letsswtich.factory

import android.util.EventLog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.data.Events
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.map.eventDetail.EventDetailViewModel

class EventViewModelFactory constructor(
    private val repository: LetsSwitchRepository,
    private val eventDetail: Events

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(EventDetailViewModel::class.java) ->
                    EventDetailViewModel(repository, eventDetail)


                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}