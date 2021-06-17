package com.peter.letsswtich.home

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.Requirement
import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.data.source.remote.LetsSwitchRemoteDataSource
import com.peter.letsswtich.ext.excludeUser
import com.peter.letsswtich.ext.filterByTraits
import com.peter.letsswtich.login.UserManager

import com.peter.letsswtich.network.LoadApiStatus
import com.peter.letsswtich.util.Logger
import com.peter.letsswtich.util.ServiceLocator.letsSwitchRepository
import kotlinx.coroutines.*
import java.util.logging.Handler
import kotlin.coroutines.suspendCoroutine

class HomeViewModel(private val letsSwitchRepository: LetsSwitchRepository) : ViewModel() {

    private val _allUser = MutableLiveData<List<User>>()

    val allUser: MutableLiveData<List<User>>
        get() = _allUser

    private var _redBg = MutableLiveData<Float>()

    val redBg: LiveData<Float>
        get() = _redBg

    private var _blueBg = MutableLiveData<Float>()

    val blueBg: LiveData<Float>
        get() = _blueBg

    val _snapPosition = MutableLiveData<Int>()
    val snapPosition: MutableLiveData<Int>
        get() = _snapPosition

    private val _userLikeList = MutableLiveData<List<String>>()

    val userLikeList: LiveData<List<String>>
        get() = _userLikeList


    private val _requirement = MutableLiveData<Requirement>()

    val requirement: LiveData<Requirement>
        get() = _requirement


    var count: Boolean = false

    var matchList = MutableLiveData<List<User>>()


    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    // list of users after filtering
    private val _usersWithMatch = MutableLiveData<List<User>>()

    val usersWithMatch: LiveData<List<User>>
        get() = _usersWithMatch

    private val _oldMatchList = MutableLiveData<List<User>>()

    val oldMatchList: MutableLiveData<List<User>>
        get() = _oldMatchList

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

    val decoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0
            } else {
                outRect.left =
                    LetsSwtichApplication.instance.resources.getDimensionPixelSize(R.dimen.space_detail_circle)
            }
        }
    }

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")
        getRequirement(UserManager.user.email)
        getNewMatchListener(UserManager.user.email)
    }

    fun updateMyLike(myEmail: String, user: User) {
        coroutineScope.launch {

            when (val result = letsSwitchRepository.updateMyLike(myEmail, user)) {
                is Result.Success -> {
                    _error.value = null
                    leave(true)
                }
                is Result.Fail -> {
                    _error.value = result.error
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                }
                else -> {
                    _error.value =
                        LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                }

            }
        }
    }

    fun updateMatch(myEmail: String, user: User) {
        Log.d("HomeViewModel", "UpdateMatch has run!!!!!!!")
        coroutineScope.launch {

            when (val result = letsSwitchRepository.updateMatch(myEmail, user)) {
                is Result.Success -> {
                    _error.value = null
                    leave(true)
                }
                is Result.Fail -> {
                    _error.value = result.error
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                }
                else -> {
                    _error.value =
                        LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                }

            }
        }
    }

    fun removeUserFromChatList(myEmail: String, friendEmail: String) {
        Log.d("HomeViewModel", "RemoveList ChatList has run!!!!!!!")
        coroutineScope.launch {

            when (val result = letsSwitchRepository.removeFromChatList(myEmail, friendEmail)) {
                is Result.Success -> {
                    _error.value = null
                    leave(true)
                }
                is Result.Fail -> {
                    _error.value = result.error
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                }
                else -> {
                    _error.value =
                        LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                }

            }
        }
    }

    fun removeFromLikeList(myEmail: String, user: User) {
//        Log.d("HomeViewModel","RemoveList has run!!!!!!!")
        coroutineScope.launch {

            when (val result = letsSwitchRepository.removeUserFromLikeList(myEmail, user)) {
                is Result.Success -> {
                    _error.value = null
                    leave(true)
                }
                is Result.Fail -> {
                    _error.value = result.error
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                }
                else -> {
                    _error.value =
                        LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                }

            }
        }
    }


    fun getRequirement(myEmail: String) {
        coroutineScope.launch {

            val result = letsSwitchRepository.getRequirement(myEmail)

            _requirement.value = when (result) {
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
                    _error.value =
                        LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                    null
                }
            }
            _refreshStatus.value = false

        }
    }


    fun getLikeList(myEmail: String, user: User) {
//        Log.d("HomeViewModel", "GetLxikeList has run!!")
        coroutineScope.launch {

            val result = letsSwitchRepository.getLikeList(myEmail, user)

            _userLikeList.value = when (result) {
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
                    _error.value =
                        LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                    null
                }
            }
            _refreshStatus.value = false

        }
    }

    fun getNewMatchListener(myEmail: String) {
        Log.d("HomeViewModel", "getNewMatchListener has run!!!")
        matchList = letsSwitchRepository.getNewMatchListener(myEmail)
    }

    fun getMyOldMatchList(myEmail: String) {
        coroutineScope.launch {

            Log.d("HomeViewModel", "getMyOldMatchList has run!!!")

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
                    _error.value =
                        LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                    null
                }
            }
            _refreshStatus.value = false
        }
    }


    fun getAllUser() {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            val result = letsSwitchRepository.getAllUser()
            _allUser.value = when (result) {
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
            _refreshStatus.value = false
        }
    }

    var doneProgressCount = 4
    private fun doneProgress() {

        doneProgressCount--
        if (doneProgressCount == 0) _status.value = LoadApiStatus.DONE
    }


    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }

    fun setRedBg(ratio: Float) {
        _redBg.value = ratio
    }

    fun setBlueBg(ratio: Float) {
        _blueBg.value = ratio
    }

    fun filteredUserList(users: List<User>) {
        _usersWithMatch.value = users.filterByTraits(requirement.value!!)

        for (i in _usersWithMatch.value!!) {
            Log.d("HomeViewModel", "value of users = ${i.name}")
        }
    }

    fun onCampaignScrollChange(
        layoutManager: RecyclerView.LayoutManager?,
        linearSnapHelper: LinearSnapHelper
    ) {
        val snapView = linearSnapHelper.findSnapView(layoutManager)
        snapView?.let {
            layoutManager?.getPosition(snapView)?.let {
                if (it != snapPosition.value) {
                    _snapPosition.value = it
                    Log.i("snapPosition on scrollChange", "$it")
                }
            }
        }
    }
}