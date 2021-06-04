package com.peter.letsswtich.editprofile.preview

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository

class PreviewViewModel(private val letsSwitchRepository: LetsSwitchRepository):ViewModel() {

    var userDetail = User()
    var userImages = listOf<String>()
    val fake = listOf("https://img.onl/3zX6",
        "https://api.appworks-school.tw/assets/201807202150/main.jpg",
        "https://api.appworks-school.tw/assets/201807201824/main.jpg",
        "https://api.appworks-school.tw/assets/201807201824/main.jpg")

    val _snapPosition = MutableLiveData<Int>()
    val snapPosition: MutableLiveData<Int>
        get() = _snapPosition

    val decoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0
            } else {
                outRect.left = LetsSwtichApplication.instance.resources.getDimensionPixelSize(R.dimen.space_detail_circle)
            }
        }
    }

    fun onCampaignScrollChange(layoutManager: RecyclerView.LayoutManager?, linearSnapHelper: LinearSnapHelper) {
        val snapView = linearSnapHelper.findSnapView(layoutManager)
        snapView?.let {
            layoutManager?.getPosition(snapView)?.let {
                if (it != snapPosition.value) {
                    _snapPosition.value = it
                    Log.i("snapPosition on scrollChange","$it")
                }
            }
        }
    }
}