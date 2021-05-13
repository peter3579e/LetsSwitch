package com.peter.letsswtich.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.network.LoadApiStatus
import kotlinx.coroutines.*

class HomeViewModel(private val letsSwitchRepository: LetsSwitchRepository): ViewModel() {

    private val _cardProduct = MutableLiveData<List<User>>()

    val cardProduct: MutableLiveData<List<User>>

        get() = _cardProduct

    private var _redBg = MutableLiveData<Float>()

    val redBg: LiveData<Float>
        get() = _redBg

    private var _blueBg = MutableLiveData<Float>()

    val blueBg: LiveData<Float>
        get() = _blueBg

    private val _usersWithMatch = MutableLiveData<List<User>>()

    val usersWithMatch: LiveData<List<User>>
        get() = _usersWithMatch

    private val _navigateToProfilePage = MutableLiveData<Boolean>()

    val  navigateToProfilePage: MutableLiveData<Boolean>

        get() = _navigateToProfilePage


    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

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

    init {
        _cardProduct.value = null
    }




    fun getUserItem() {
        coroutineScope.launch {
            _cardProduct.value = letsSwitchRepository.getUserItem()
            Log.d("Peter","Value of getUser ${cardProduct.value}")
        }
    }

    fun navigateToProfile(){
        _navigateToProfilePage.value = true
    }

    fun onProfileNavigated() {
        _navigateToProfilePage.value = false
    }



    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }

    fun setRedBg (ratio : Float) {
        _redBg.value = ratio
    }

    fun setBlueBg (ratio: Float) {
        _blueBg.value = ratio
    }

    fun createSortedList() {
        _usersWithMatch.value = _cardProduct.value
    }


}