package com.peter.letsswtich.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.Requirement
import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.ext.filterByTraits

import com.peter.letsswtich.network.LoadApiStatus
import com.peter.letsswtich.util.Logger
import com.peter.letsswtich.util.ServiceLocator.letsSwitchRepository
import kotlinx.coroutines.*

class HomeViewModel(private val letsSwitchRepository: LetsSwitchRepository,  requirement: Requirement): ViewModel() {

    val answer = requirement

    private val _allUser = MutableLiveData<List<User>>()

    val allUser: MutableLiveData<List<User>>

        get() = _allUser

    private var _redBg = MutableLiveData<Float>()

    val redBg: LiveData<Float>
        get() = _redBg

    private var _blueBg = MutableLiveData<Float>()

    val blueBg: LiveData<Float>
        get() = _blueBg

    private val _likedUser = MutableLiveData<List<User>>()

    val likedUser: LiveData<List<User>>
        get() = _likedUser



    var count: Boolean = false




//    private val _navigateToProfilePage = MutableLiveData<Boolean>()
//
//    val  navigateToProfilePage: MutableLiveData<Boolean>
//
//        get() = _navigateToProfilePage

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    // list of users after filtering
    private val _usersWithMatch = MutableLiveData<List<User>>()

    val usersWithMatch: LiveData<List<User>>
        get() = _usersWithMatch


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
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")


        getAllUser()
    }

    fun updateAndCheckLike(myEmail: String, user: User){
        coroutineScope.launch {

            when(val result = letsSwitchRepository.updateAndCheckLike(myEmail,user)){
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value = LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                    _status.value = LoadApiStatus.ERROR
                }
                    
                }
            }
        }




    fun getAllUser() {
       coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            val result = letsSwitchRepository.getAllUser()

            _allUser.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    doneProgress()
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
//            Log.d("HomeViewModel","Value of GetAllUser = ${_allUser.value}")
            _refreshStatus.value = false
        }
    }

    var doneProgressCount = 4
    private fun doneProgress() {

        doneProgressCount--
        if (doneProgressCount == 0) _status.value = LoadApiStatus.DONE
    }

//    fun navigateToProfile(){
//        _navigateToProfilePage.value = true
//    }
//
//    fun onProfileNavigated() {
//        _navigateToProfilePage.value = false
//    }



    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }

    fun setRedBg (ratio : Float) {
        _redBg.value = ratio
    }

    fun setBlueBg (ratio: Float) {
        _blueBg.value = ratio
    }

    fun filteredUserList(users: List<User>){
        _usersWithMatch.value = users.filterByTraits(answer)
        Log.d("HomeViewModel","value of users = $users")
        Log.d("HomeViewModel","here here here!!!")
    }




}