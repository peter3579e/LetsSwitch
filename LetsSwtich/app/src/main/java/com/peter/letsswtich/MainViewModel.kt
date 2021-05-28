package com.peter.letsswtich

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.util.CurrentFragmentType
import com.peter.letsswtich.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(private val letsSwitchRepository: LetsSwitchRepository):ViewModel() {

    var matchList = MutableLiveData<List<User>>()

    private val _oldMatchList = MutableLiveData<List<User>>()

    val oldMatchList: MutableLiveData<List<User>>
        get() = _oldMatchList

    val likeList = MutableLiveData<User>()

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    val _navigateToFriendsProfile = MutableLiveData<Boolean>()
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

        getMyOldMatchList(UserManager.user.email)
        getNewMatchListener(UserManager.user.email)

    }

    fun getNewMatchListener(myEmail: String){
        Log.d("HomeViewModel","getNewMatchListener has run!!!")
        matchList = letsSwitchRepository.getNewMatchListener(myEmail)
    }

    fun getMyOldMatchList(myEmail: String){
        coroutineScope.launch {

            Log.d("HomeViewModel","getMyOldMatchList has run!!!")

            val result = letsSwitchRepository.getMyOldMatchList(myEmail)

            _oldMatchList.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    null
                }
                else -> {
                    _error.value = LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                    null
                }
            }
//            Log.d("HomeViewModel","Value of GetAllUser = ${_allUser.value}")
            _refreshStatus.value = false
        }
    }

    fun navigateToFriendProfile(){
        _navigateToFriendsProfile.value = true
    }

    fun friendsProfileNavigated(){
        _navigateToFriendsProfile.value = false
    }

}