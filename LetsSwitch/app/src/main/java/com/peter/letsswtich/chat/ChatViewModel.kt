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

    var newestFriendDetail : List<User>? = null

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

    init {

        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")


        getLiveChatList(UserManager.user.email)
    }

    private fun getLiveChatList(myEmail: String){
        allLiveChatRooms = letsSwitchRepository.getLiveChatList(myEmail)
        _status.value = LoadApiStatus.DONE
    }

    fun getChatRoom(): List<ChatRoom>{
        val chatList: MutableList<UserInfo> = mutableListOf()
        val chatRoom :MutableList<ChatRoom> = mutableListOf()
        _matchList.value!!.forEach { it ->
            val myInfo = UserInfo().apply {
                userEmail = UserManager.user.email
                userImage = UserManager.user.personImages[0]
                userName = UserManager.user.name
            }

            val friendInfo = UserInfo().apply {
                userEmail = it.email
                userName = it.name
                userImage = it.personImages[0]
            }

            chatList.add(friendInfo)
            val attendList = listOf<String>(UserManager.user.email,it.email)
            val chat = ChatRoom()

            chat.apply {
                chatRoomId = ""
                latestTime = 0
                latestMessageTime = 0
                attendeesInfo = listOf(myInfo,friendInfo)
                attendees = attendList
            }
            postChatList(chat)
            chatRoom.add(chat)
        }
        Log.d("ChatViewModel","value of ChatRoom $chatRoom")
        return chatRoom
    }

    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }

    fun postChatList(chatRoom: ChatRoom){
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
}