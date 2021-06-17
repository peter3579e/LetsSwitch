package com.peter.letsswtich.profile

import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.source.LetsSwitchRepository

class PicViewModel(private val letsSwitchRepository: LetsSwitchRepository, userImage:String):ViewModel() {

    val image = userImage
}