package com.peter.letsswtich.map.event

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.source.LetsSwitchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.sql.Time
import java.util.*

class EditEventViewModel(private val letsSwitchRepository: LetsSwitchRepository):ViewModel() {

    val locationName = MutableLiveData<String>()

    val enterTitle = MutableLiveData<String>()

    val photoList = MutableLiveData<MutableList<String>>()

    val enterDetail = MutableLiveData<String>()

    val selectedDate = MutableLiveData<String>()

    val selectedTime = MutableLiveData<String>()

    private val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date

    private val _time = MutableLiveData<Time>()
    val time : LiveData<Time>
        get() = _time

    val _photoUri = MutableLiveData<Uri>()
    val photoUri: LiveData<Uri>
        get() = _photoUri

    val camera = MutableLiveData<Boolean>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        setCurrentDate(Date())
    }


    fun setPhoto(photo: Uri){
        _photoUri.value = photo
    }

    fun setCurrentDate(date: Date){
        _date.value = date
        _time.value = Time(date.time)
    }


    fun startCamera () {
        camera.value = true
    }

    fun closeCamera () {
        camera.value = false
    }
}