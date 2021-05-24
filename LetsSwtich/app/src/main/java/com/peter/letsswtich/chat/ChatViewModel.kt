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

    private val _leave = MutableLiveData<Boolean>()


    val leave: LiveData<Boolean>
        get() = _leave

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

        getMatchList(UserManager.user.email)
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

    fun getChatRoom(): List<ChatRoom>{

        Log.d("ChatViewModel","getChatRoom has run!!")

        var chatList: MutableList<UserInfo> = mutableListOf()
//        var attendeesList : MutableList<String> = mutableListOf()
        var chatRoom :MutableList<ChatRoom> = mutableListOf()
        matchList.value!!.forEach { it ->
            val myInfo = UserInfo().apply {
                userEmail = UserManager.user.email
                userImage = UserManager.user.bigheadPic
                userName = UserManager.user.name
            }

            val friendInfo = UserInfo().apply {
                userEmail = it.email
                userName = it.name
                userImage = it.bigheadPic
            }

            chatList.add(friendInfo)
            val attendlist = listOf<String>(UserManager.user.email,it.email)
            val chat = ChatRoom()

            chat.apply {
                chatRoomId = ""
                latestTime = 0
                attendeesInfo = listOf(myInfo,friendInfo)
                attendees = attendlist
            }
            postChatList(chat)
            chatRoom.add(chat)
        }

        Log.d("ChatViewModel","value of ChatRoom $chatRoom")

        return chatRoom
    }

    fun postChatList(chatRoom: ChatRoom){
        Log.d("ChatViewModel","postChatList has run!!")
        coroutineScope.launch {
            when (val result = letsSwitchRepository.postChatRoom(chatRoom)) {
                is com.peter.letsswtich.data.Result.Success -> {
                    _error.value = null
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
                    _error.value = LetsSwtichApplication.instance.getString(R.string.get_nothing_from_firebase)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }



    fun getMatchList(myEmail: String){
        coroutineScope.launch {

            Log.d("ChatViewModel","getMyOldMatchList has run!!!")

            val result = letsSwitchRepository.getMyOldMatchList(myEmail)

            _matchList.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    null
                }
                else -> {
                    _error.value = LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
                    null
                }
            }
//            Log.d("HomeViewModel","Value of GetAllUser = ${_allUser.value}")
        }
    }

    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }




}