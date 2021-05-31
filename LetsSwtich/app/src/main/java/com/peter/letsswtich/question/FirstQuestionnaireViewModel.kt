package com.peter.letsswtich.question

import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.source.LetsSwitchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FirstQuestionnaireViewModel(private val letsSwitchRepository: LetsSwitchRepository):ViewModel() {


    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun postUser(){
        coroutineScope.launch {
            letsSwitchRepository.postfake()
        }
    }
}