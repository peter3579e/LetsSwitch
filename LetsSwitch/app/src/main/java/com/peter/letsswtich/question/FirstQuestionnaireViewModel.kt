package com.peter.letsswtich.question

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FirstQuestionnaireViewModel(private val letsSwitchRepository: LetsSwitchRepository) :
    ViewModel() {

    val usrInfo = UserManager.user

    private val _selectedAge = MutableLiveData<Int>()

    val selectedAge: LiveData<Int>
        get() = _selectedAge

    private val _selectedGender = MutableLiveData<String>()

    val selectedGender: LiveData<String>
        get() = _selectedGender

    private val _selectedCity = MutableLiveData<String>()
    val selectedCity: LiveData<String>
        get() = _selectedCity

    private val _selectedMothertongue = MutableLiveData<String>()

    val selectedMothertongue: LiveData<String>
        get() = _selectedMothertongue

    private val _selectedFluent = MutableLiveData<String>()

    val selectedFluent: LiveData<String>
        get() = _selectedFluent

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()


    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    fun postUser(user: User) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = letsSwitchRepository.postUser(user)) {
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

    fun setupCity(city: String) {
        _selectedCity.value = city
    }

    fun setupAge(age: Int) {
        _selectedAge.value = age
    }

    fun setupGender(gender: String) {
        _selectedGender.value = gender
    }

    fun setupMothertongue(language: String) {
        _selectedMothertongue.value = language
    }

    fun setupFluent(language: String) {
        _selectedFluent.value = language
    }
}