package com.peter.letsswtich.map.eventDetail

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.Events
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val letsSwitchRepository: LetsSwitchRepository,
    eventDetail: Events
) : ViewModel() {

    val event = eventDetail
    val photoList = mutableListOf<String>()

    val _snapPosition = MutableLiveData<Int>()
    val snapPosition: MutableLiveData<Int>
        get() = _snapPosition

    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    var jointList = MutableLiveData<List<User>>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val decoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0
            } else {
                outRect.left =
                    LetsSwtichApplication.instance.resources.getDimensionPixelSize(R.dimen.space_detail_circle)
            }
        }
    }

    val _navigateBackToMap = MutableLiveData<Boolean>()
    val navigateBackToMap: LiveData<Boolean>
        get() = _navigateBackToMap

    val _join = MutableLiveData<Boolean>()
    val join: LiveData<Boolean>
        get() = _join

    init {
        getLiveJoinList(event)
    }

    fun navigateBackToMap() {
        _navigateBackToMap.value = true
    }

    fun mapNavigated() {
        _navigateBackToMap.value = false
    }

    fun onCampaignScrollChange(
        layoutManager: RecyclerView.LayoutManager?,
        linearSnapHelper: LinearSnapHelper
    ) {
        val snapView = linearSnapHelper.findSnapView(layoutManager)
        snapView?.let {
            layoutManager?.getPosition(snapView)?.let {
                if (it != snapPosition.value) {
                    _snapPosition.value = it
                    Log.i("snapPosition on scrollChange", "$it")
                }
            }
        }
    }

    fun sendJoin() {
        _join.value = true
    }

    fun joinSent() {
        _join.value = false
    }

    private fun getLiveJoinList(events: Events) {
        jointList = letsSwitchRepository.getLiveJoinList(events)
        _status.value = LoadApiStatus.DONE
    }

    fun postJoin(myEmail: String, events: Events) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = letsSwitchRepository.postJoin(myEmail, events)) {
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
}