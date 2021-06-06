package com.peter.letsswtich.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.component.GridSpacingItemDecoration
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.network.LoadApiStatus
import com.peter.letsswtich.util.ServiceLocator.letsSwitchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*

class ProfileViewModel(private val letsSwitchRepository: LetsSwitchRepository, user: com.peter.letsswtich.data.User,fromMap:Boolean):ViewModel() {

    val userDetail = user

    val ifMap = fromMap

    val locatMe = MutableLiveData<Boolean>()

    val mylocation = MutableLiveData<LatLng>()

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

    private val _camera = MutableLiveData<Boolean>()

    val camera: LiveData<Boolean>
        get() = _camera

    private val _setting = MutableLiveData<Boolean>()

    val setting: LiveData<Boolean>
        get() = _setting

    private val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date

    private val _time = MutableLiveData<Time>()
    val time : LiveData<Time>
        get() = _time

    private val _isUploadPhoto = MutableLiveData<Boolean>()
    private val isUploadPhoto: LiveData<Boolean>
        get() = _isUploadPhoto


    private val _navigateToEditProfile = MutableLiveData<Boolean>()

    val navigateToEditProfile: LiveData<Boolean>
        get() = _navigateToEditProfile

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    val _photoUri = MutableLiveData<Uri>()
    val photoUri: LiveData<Uri>
        get() = _photoUri

    init {
        setCurrentDate(Date())
    }

    fun uploadPhoto(){
        _isUploadPhoto.value = true
    }

    fun setPhoto(photo: Uri){
        _photoUri.value = photo
    }

    fun setCurrentDate(date: Date){
        _date.value = date
        _time.value = Time(date.time)
    }



    fun navigateToEditProfile(){
        _navigateToEditProfile.value = true
    }

    fun editProfileNavigated(){
        _navigateToEditProfile.value = false
    }

    val decoration = GridSpacingItemDecoration(
            4,
            LetsSwtichApplication.instance.resources.getDimensionPixelSize(R.dimen.space_photo_grid), true
    )

    fun startCamera () {
        _camera.value = true
    }

    fun closeCamera() {
        _camera.value = false
    }

    fun navigateToSetting(){
       _setting.value = true
    }

    fun settingNavigated(){
        _setting.value = false
    }

    fun updateUser(user: User) {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = letsSwitchRepository.updateUser(user)) {
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