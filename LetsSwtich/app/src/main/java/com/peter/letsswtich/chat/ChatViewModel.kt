package com.peter.letsswtich.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.data.source.LetsSwitchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChatViewModel(private val letsSwitchRepository: LetsSwitchRepository):ViewModel() {


    var allLiveChatRooms = MutableLiveData<List<ChatRoom>>()

    private val _filteredChatRooms = MutableLiveData<List<ChatRoom>>()

    val filteredChatRooms : LiveData<List<ChatRoom>>
        get() = _filteredChatRooms

    // the Coroutine runs using the Main (UI) dispatcher
    val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    fun createFilteredChatRooms(filteredChatRoom: List<ChatRoom>) {
        _filteredChatRooms.value = filteredChatRoom
    }

    fun getChatItem() {
        coroutineScope.launch {
            allLiveChatRooms.value = letsSwitchRepository.getChatItem()
            Log.d("Peter","Value of getUser ${allLiveChatRooms.value}")
        }
    }
}