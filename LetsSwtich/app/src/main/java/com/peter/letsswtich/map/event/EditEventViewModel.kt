package com.peter.letsswtich.map.event

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.Events
import com.peter.letsswtich.data.Location
import com.peter.letsswtich.data.Message
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*

class EditEventViewModel(private val letsSwitchRepository: LetsSwitchRepository) : ViewModel() {

    val locationDetail = MutableLiveData<Location>()

    val enterTitle = MutableLiveData<String>()

    val photoList = MutableLiveData<MutableList<String>>()

    val enterDetail = MutableLiveData<String>()

    val selectedDate = MutableLiveData<String>()

    val selectedTime = MutableLiveData<String>()

    private val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    private val _time = MutableLiveData<Time>()
    val time: LiveData<Time>
        get() = _time

    private val _selectedPeople = MutableLiveData<Int>()
    val selectedPeople: LiveData<Int>
        get() = _selectedPeople

    val _photoUri = MutableLiveData<Uri>()
    val photoUri: LiveData<Uri>
        get() = _photoUri

    val _navigateBackToMap = MutableLiveData<Boolean>()
    val navigateBackToMap: LiveData<Boolean>
        get() = _navigateBackToMap

    val camera = MutableLiveData<Boolean>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        setCurrentDate(Date())
    }

    fun postEvent(events: Events) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = letsSwitchRepository.postEvent(events)) {
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

    fun navigateBackToMap() {
        _navigateBackToMap.value = true
    }

    fun mapNavigated() {
        _navigateBackToMap.value = false
    }

    fun setPhoto(photo: Uri) {
        _photoUri.value = photo
    }

    fun setCurrentDate(date: Date) {
        _date.value = date
        _time.value = Time(date.time)
    }

    fun startCamera() {
        camera.value = true
    }

    fun setupPeople(age: Int) {
        _selectedPeople.value = age
    }

    fun closeCamera() {
        camera.value = false
    }

    fun getEvents(): Events {
        return Events(
            eventId = "",
            eventTitle = enterTitle.value!!,
            eventDetail = enterDetail.value!!,
            Location = locationDetail.value!!,
            date = selectedDate.value!!,
            time = selectedTime.value!!,
            peopleNumber = selectedPeople.value!!,
            eventPhotos = photoList.value!!,
            postBy = UserManager.user.name,
            postTime = 0L
        )
    }

}