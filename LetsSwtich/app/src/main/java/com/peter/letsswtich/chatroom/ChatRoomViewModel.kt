package com.peter.letsswtich.chatroom

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.Message
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChatRoomViewModel(private val letsSwitchRepository: LetsSwitchRepository, userEmail:String, userName:String) : ViewModel() {

    val currentChattingUser = userEmail

    val currentChattingName = userName

    // EditText input
    val enterMessage = MutableLiveData<String>()

    // All live message
    var allLiveMessage = MutableLiveData<List<Message>>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

        getMessageItem()
    }



    fun getMessageItem() {
        coroutineScope.launch {
            allLiveMessage.value = letsSwitchRepository.getMessageItem()
            Log.d("ChatRoomViewModel","Value of getMessage ${allLiveMessage.value}")
        }
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