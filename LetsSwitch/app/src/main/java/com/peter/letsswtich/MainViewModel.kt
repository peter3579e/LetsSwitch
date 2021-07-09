package com.peter.letsswtich

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.*
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.network.LoadApiStatus
import com.peter.letsswtich.util.CurrentFragmentType
import com.peter.letsswtich.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(private val letsSwitchRepository: LetsSwitchRepository) : ViewModel() {

    var matchList = MutableLiveData<List<User>>()

    val requirement = MutableLiveData<Requirement>()

    val newestFriendDetail = MutableLiveData<List<User>>()

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()


    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status


    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    val userDetail = MutableLiveData<User>()

    private val _userInfo = MutableLiveData<User>()

    val userInfo: LiveData<User>
        get() = _userInfo


    private val _navigateToFriendsProfile = MutableLiveData<Boolean>()
    val navigateToFriendsProfile: MutableLiveData<Boolean>
        get() = _navigateToFriendsProfile

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

        getNewMatchListener(UserManager.user.email)
        getUserDetail(UserManager.user.email)

    }

    private fun getNewMatchListener(myEmail: String) {
        Log.d("HomeViewModel", "getNewMatchListener has run!!!")
        matchList = letsSwitchRepository.getNewMatchListener(myEmail)
    }

    fun getUserDetail(userEmail: String) {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            val result = letsSwitchRepository.getUserDetail(userEmail)

            _userInfo.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
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
                    _error.value =
                        LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }


    fun navigateToFriendProfile() {
        _navigateToFriendsProfile.value = true
    }

    fun friendsProfileNavigated() {
        _navigateToFriendsProfile.value = false
    }
}