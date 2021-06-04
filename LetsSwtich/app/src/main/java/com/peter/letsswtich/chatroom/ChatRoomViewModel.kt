package com.peter.letsswtich.chatroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.Message
import com.peter.letsswtich.data.MessageWithId
import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.data.source.remote.LetsSwitchRemoteDataSource.postMessage
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.network.LoadApiStatus
import com.peter.letsswtich.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChatRoomViewModel(private val letsSwitchRepository: LetsSwitchRepository, userEmail:String, userName: String) : ViewModel() {

    val currentChattingUser = userEmail

    val currentChattingName = userName

    // EditText input
    var enterMessage = MutableLiveData<String>()

    private val _userDetail = MutableLiveData<User>()

    val userDetail: LiveData<User>
        get() = _userDetail

    var count :Int =0

    var filterMessage = listOf<Message>()



    // All live message
    var allLiveMessage = MutableLiveData<MessageWithId>()

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

        getAllLiveMessage(getUserEmails(UserManager.user.email, currentChattingUser))

        getUserDetail(currentChattingUser)

//        getMessageItem()
    }

    fun getUserEmails(user1Email: String, user2Email: String): List<String> {
        return listOf(user1Email, user2Email)
    }

    fun getMessage(): Message {
        return Message(
                id = "",
                senderName = UserManager.user.name,
                senderImage = UserManager.user.personImages[0],
                senderEmail = UserManager.user.email,
                text = enterMessage.value.toString(),
                createdTime = 0L,
            read = false
        )
    }

    fun postMessage(userEmails: List<String>, message: Message) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = letsSwitchRepository.postMessage(userEmails, message)) {
                is com.peter.letsswtich.data.Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)
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

    fun updateIsRead(friendEmail:String,documentId:String){
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = letsSwitchRepository.updateIsRead(friendEmail,documentId)) {
                is com.peter.letsswtich.data.Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    leave(true)
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

    fun getUserDetail(userEmail:String) {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            val result = letsSwitchRepository.getUserDetail(userEmail)

            _userDetail.value = when (result) {
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
                    _error.value = LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
//            Log.d("HomeViewModel","Value of GetAllUser = ${_allUser.value}")
        }
    }

    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }




//    fun getMessageItem() {
//        coroutineScope.launch {
//            allLiveMessage.value = letsSwitchRepository.getMessageItem()
//            Log.d("ChatRoomViewModel","Value of getMessage ${allLiveMessage.value}")
//        }
//    }

    private fun getAllLiveMessage(userEmails: List<String>) {
        allLiveMessage = letsSwitchRepository.getAllLiveMessage(userEmails)
        _status.value = LoadApiStatus.DONE
    }

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}