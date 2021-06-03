package com.peter.letsswtich

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.UserInfo
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.network.LoadApiStatus
import com.peter.letsswtich.util.CurrentFragmentType
import com.peter.letsswtich.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(private val letsSwitchRepository: LetsSwitchRepository):ViewModel() {

    var matchList = MutableLiveData<List<User>>()

    private val _oldMatchList = MutableLiveData<List<User>>()

    val oldMatchList: MutableLiveData<List<User>>
        get() = _oldMatchList

    val likeList = MutableLiveData<User>()

    private val _leave = MutableLiveData<Boolean>()


    val leave: LiveData<Boolean>
        get() = _leave

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()


    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status


    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    val _navigateToFriendsProfile = MutableLiveData<Boolean>()
    val navigateToFriendsProfile: MutableLiveData<Boolean>
    get() = _navigateToFriendsProfile

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

//        getMyOldMatchList(UserManager.user.email)
        getNewMatchListener(UserManager.user.email)

    }

    fun getNewMatchListener(myEmail: String){
        Log.d("HomeViewModel","getNewMatchListener has run!!!")
        matchList = letsSwitchRepository.getNewMatchListener(myEmail)
    }

    fun getChatRoom(): List<ChatRoom>{

        Log.d("ChatViewModel","getChatRoom has run!!")

        var chatList: MutableList<UserInfo> = mutableListOf()
//        var attendeesList : MutableList<String> = mutableListOf()
        var chatRoom :MutableList<ChatRoom> = mutableListOf()
        matchList.value!!.forEach { it ->
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
            val attendlist = listOf<String>(UserManager.user.email,it.email)
            val chat = ChatRoom()

            chat.apply {
                chatRoomId = ""
                latestTime = 0
                latestMessageTime = 0
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

    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }



//    fun getMatchList(myEmail: String){
//        coroutineScope.launch {
//
//            Log.d("ChatViewModel","getMyOldMatchList has run!!!")
//
//            val result = letsSwitchRepository.getMyOldMatchList(myEmail)
//
//            _matchList.value = when (result) {
//                is Result.Success -> {
//                    _error.value = null
//                    result.data
//                }
//                is Result.Fail -> {
//                    _error.value = result.error
//                    null
//                }
//                is Result.Error -> {
//                    _error.value = result.exception.toString()
//                    null
//                }
//                else -> {
//                    _error.value = LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
//                    null
//                }
//            }
////            Log.d("HomeViewModel","Value of GetAllUser = ${_allUser.value}")
//        }
//    }

//    fun getMyOldMatchList(myEmail: String){
//        coroutineScope.launch {
//
//            Log.d("HomeViewModel","getMyOldMatchList has run!!!")
//
//            val result = letsSwitchRepository.getMyOldMatchList(myEmail)
//
//            _oldMatchList.value = when (result) {
//                is Result.Success -> {
//                    _error.value = null
//                    result.data
//                }
//                is Result.Fail -> {
//                    _error.value = result.error
//                    null
//                }
//                is Result.Error -> {
//                    _error.value = result.exception.toString()
//                    null
//                }
//                else -> {
//                    _error.value = LetsSwtichApplication.appContext.getString(R.string.get_nothing_from_firebase)
//                    null
//                }
//            }
////            Log.d("HomeViewModel","Value of GetAllUser = ${_allUser.value}")
//            _refreshStatus.value = false
//        }
//    }

    fun navigateToFriendProfile(){
        _navigateToFriendsProfile.value = true
    }

    fun friendsProfileNavigated(){
        _navigateToFriendsProfile.value = false
    }

}