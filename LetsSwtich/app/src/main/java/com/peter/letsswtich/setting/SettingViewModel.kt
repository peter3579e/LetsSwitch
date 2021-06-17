package com.peter.letsswtich.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.Requirement
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SettingViewModel(
    private val letsSwitchRepository: LetsSwitchRepository,
    requirement: Requirement
) : ViewModel() {

    val need = requirement

    private val _profile = MutableLiveData<Boolean>()

    val profile: LiveData<Boolean>
        get() = _profile

    val maxAge = MutableLiveData<Int>()
    val minAge = MutableLiveData<Int>()

    private val _selectedGender = MutableLiveData<String>()

    val selectedGender: MutableLiveData<String>
        get() = _selectedGender

    private val _selectedFirstLanguage = MutableLiveData<String>()

    val selectedFirstLanguage: LiveData<String>
        get() = _selectedFirstLanguage

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _city = MutableLiveData<String>()

    val city: LiveData<String>
        get() = _city


    fun navigateToProfile() {
        _profile.value = true
    }

    fun profileNavigated() {
        _profile.value = false
    }

    fun setupGender(gender: String) {
        _selectedGender.value = gender
    }

    fun setupMothertongue(language: String) {
        _selectedFirstLanguage.value = language
    }

    fun setupFluent(language: String) {
        _city.value = language
    }

    fun postRequirement(myEmail: String, require: Requirement) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = letsSwitchRepository.postRequirement(myEmail, require)) {
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