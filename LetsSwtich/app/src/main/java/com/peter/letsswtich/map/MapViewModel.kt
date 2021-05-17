package com.peter.letsswtich.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.Comment
import com.peter.letsswtich.data.StoreLocation
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MapViewModel(private val letsSwitchRepository: LetsSwitchRepository):ViewModel() {

    private val _selectStore = MutableLiveData<StoreLocation>()

    val selectStore: LiveData<StoreLocation>
        get() = _selectStore

    val storeCardStatus = MutableLiveData<Boolean>().apply {
        value = false
    }

    val storeDrinkStatus = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _storeLocation = MutableLiveData<List<StoreLocation>>()

    val storeLocation: LiveData<List<StoreLocation>>
        get() = _storeLocation

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun selectStore(storeLocation: StoreLocation) {
        _selectStore.value = storeLocation
        _storeComment.value = null
    }

    private val _storeComment = MutableLiveData<List<Comment>>()

    val storeComment: LiveData<List<Comment>>
        get() = _storeComment

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")
        getMapItemLocation()
    }

    fun getMapItemLocation() {
        coroutineScope.launch {
            _storeLocation.value = letsSwitchRepository.getMapItem()
            Log.d("MapViewModel","Value of storeLocation = ${_storeLocation.value}")
        }
    }



    fun storeCardOpen() {
        storeCardStatus.value?.let {
            storeCardStatus.value = true
        }
        storeDrinkStatus.value?.let {
            storeDrinkStatus.value = false
        }
    }

    fun storeCardClose() {
        storeCardStatus.value?.let {
            storeCardStatus.value = false
        }
        storeDrinkStatus.value?.let {
            storeDrinkStatus.value = false
        }
    }

}

