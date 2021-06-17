package com.peter.letsswtich.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.network.LoadApiStatus
import com.peter.letsswtich.util.CurrentFragmentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(private val letsSwitchRepository: LetsSwitchRepository):ViewModel() {

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private val _firebaseUser = MutableLiveData<FirebaseUser>()

    val firebaseUser: LiveData<FirebaseUser>
        get() = _firebaseUser

    private val _privacy = MutableLiveData<Boolean>()

    val privacy: LiveData<Boolean>
        get() = _privacy



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

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

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
                    _error.value = LetsSwtichApplication.instance.getString(R.string.you_shall_not_pass)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }

    }

    fun privacychecked() {
        Log.d("LoginActivity","Run2")
        _privacy.value = true
    }


    fun loginAuth(account: GoogleSignInAccount?) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = letsSwitchRepository.firebaseAuthWithGoogle(account)

            _firebaseUser.value = when (result) {
                is com.peter.letsswtich.data.Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is com.peter.letsswtich.data.Result.Fail -> {
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
                    _error.value = LetsSwtichApplication.instance.getString(R.string.you_shall_not_pass)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }

    }


}