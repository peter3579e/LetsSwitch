package com.peter.letsswtich.dialog

import androidx.lifecycle.ViewModel
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.login.UserManager

class MatchedDialogViewModel(private val letsSwitchRepository: LetsSwitchRepository, matchedUserInfo: User):ViewModel() {

    val matchedUserBigHeadPic = matchedUserInfo.bigheadPic
    val matchedUserName = matchedUserInfo.name
    val myBigHeadPic = UserManager.user.bigheadPic

}