package com.peter.letsswtich.editprofile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.MainActivity
import com.peter.letsswtich.data.source.LetsSwitchRepository
import java.io.File
import java.sql.Time
import java.util.*

class EditViewModel(private val letsSwitchRepository: LetsSwitchRepository): ViewModel() {

    val photoList = MutableLiveData<MutableList<String>>()
    val newPhotoList = MutableLiveData<MutableList<String>>()

    private val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date

    private val _time = MutableLiveData<Time>()
    val time : LiveData<Time>
        get() = _time

    private val _isUploadPhoto = MutableLiveData<Boolean>()
    private val isUploadPhoto: LiveData<Boolean>
        get() = _isUploadPhoto

    private val _photoUri = MutableLiveData<Uri>()
    private val photoUri: LiveData<Uri>
        get() = _photoUri

    fun setPhoto(photo: Uri){
        _photoUri.value = photo
    }

    fun setCurrentDate(date: Date){
        _date.value = date
        _time.value = Time(date.time)
    }

    val camera = MutableLiveData<Boolean>()

    fun startCamera () {
        camera.value = true
    }

    fun closeCamera () {
        camera.value = false
    }

    fun uploadPhoto(){
        _isUploadPhoto.value = true
    }

}