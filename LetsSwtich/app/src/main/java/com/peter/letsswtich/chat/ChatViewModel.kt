package com.peter.letsswtich.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.UserInfo
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.network.LoadApiStatus
import com.peter.letsswtich.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChatViewModel(private val letsSwitchRepository: LetsSwitchRepository):ViewModel() {


    var allLiveChatRooms = MutableLiveData<List<ChatRoom>>()

    private val _filteredChatRooms = MutableLiveData<List<ChatRoom>>()

    val filteredChatRooms : LiveData<List<ChatRoom>>
        get() = _filteredChatRooms

    private val _roomByMessageTime = MutableLiveData<List<ChatRoom>>()


    val roomByMessageTime: MutableLiveData<List<ChatRoom>>
        get() = _roomByMessageTime

    val latestTimeMessage = MutableLiveData<Long>()

    private val _matchList = MutableLiveData<List<User>>()

    val matchList: MutableLiveData<List<User>>
        get() = _matchList

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // the Coroutine runs using the Main (UI) dispatcher
    val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    fun createFilteredChatRooms(filteredChatRoom: List<ChatRoom>) {
        _filteredChatRooms.value = filteredChatRoom
    }

//    fun getChatItem() {
//        coroutineScope.launch {
//            allLiveChatRooms.value = letsSwitchRepository.getLiveChatList()
//            Log.d("Peter","Value of getUser ${allLiveChatRooms.value}")
//        }
//    }

    init {

        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

        getLiveChatList(UserManager.user.email)
    }

    private fun getLiveChatList(myEmail: String){
        Log.d("ChatViewModel","getLiveChatList has run!!")
        allLiveChatRooms = letsSwitchRepository.getLiveChatList(myEmail)
        _status.value = LoadApiStatus.DONE
    }

//    fun getMatchList(myEmail:String){
//        Log.d("ChatViewModel","getNewMatchListener has run!!!")
//        matchList = letsSwitchRepository.getNewMatchListener(myEmail)
//    }






}