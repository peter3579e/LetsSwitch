package com.peter.letsswtich.map

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.Events
import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.network.LoadApiStatus
import com.peter.letsswtich.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*
import javax.xml.transform.dom.DOMLocator

class MapViewModel(private val letsSwitchRepository: LetsSwitchRepository) : ViewModel() {

    val showsMore = MutableLiveData<Boolean>()

    val friendslocation = MutableLiveData<LatLng>()

    val _snapPosition = MutableLiveData<Int>()
    val snapPosition: MutableLiveData<Int>
        get() = _snapPosition

    val clickedEventLocation = MutableLiveData<Events>()

    val eventsInMapReady = MutableLiveData<List<Events>>()


    val matchList = MutableLiveData<List<User>>()

    private val _userInfo = MutableLiveData<User>()

    val userInfo: LiveData<User>
        get() = _userInfo

    val listofMatchUserInfo = MutableLiveData<MutableList<User>>()

    val imagesLive = MutableLiveData<List<String>>()

    val mylocation = MutableLiveData<LatLng>()

    val clickedUserDetail = MutableLiveData<User>()

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    private val _navigateToProfile = MutableLiveData<Boolean>()

    val navigateToProfile: LiveData<Boolean>
        get() = _navigateToProfile

    private val _cardView = MutableLiveData<Int>()

    val cardView: LiveData<Int>
        get() = _cardView

    private val _event = MutableLiveData<Int>()

    val event: LiveData<Int>
        get() = _event

    private val _createEvent = MutableLiveData<Boolean>()

    val createEvent: LiveData<Boolean>
        get() = _createEvent


    val cardViewHeight = MutableLiveData<Int>()

    var allEvent = MutableLiveData<List<Events>>()

    val navigateToEventDetail = MutableLiveData<Events>()


    private val _navigateToChatRoom = MutableLiveData<Boolean>()
    val navigateToChatRoom: LiveData<Boolean>
        get() = _navigateToChatRoom


    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")
        _cardView.value = -1
        _event.value = -1
        getLiveEvent()
    }

    private fun getLiveEvent() {
        allEvent = letsSwitchRepository.getLiveEvent()
        _status.value = LoadApiStatus.DONE
    }

    fun plusEventCount() {
        _event.value = _event.value!! + 1
    }

    fun plusCount() {
        _cardView.value = _cardView.value!! + 1
    }

    fun navigateToCreateEvent() {
        _createEvent.value = true
    }

    fun createEventNavigated() {
        _createEvent.value = false
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

    fun navigateToChatRoom() {
        _navigateToChatRoom.value = true
    }

    fun chatRoomNavigated() {
        _navigateToChatRoom.value = false
    }

    fun navigateToProfile() {
        _navigateToProfile.value = true
    }

    fun profilenavigated() {
        _navigateToProfile.value = false
    }


    fun postlocaion(longitude: Double, latitude: Double, myEmail: String) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = letsSwitchRepository.postmyLocation(longitude, latitude, myEmail)) {
                is com.peter.letsswtich.data.Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                }
                is com.peter.letsswtich.data.Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is com.peter.letsswtich.data.Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value =
                        LetsSwtichApplication.instance.getString(R.string.you_shall_not_pass)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }

    }

}

