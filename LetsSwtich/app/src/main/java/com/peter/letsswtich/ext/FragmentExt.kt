package com.peter.letsswtich.ext


import androidx.fragment.app.Fragment
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.data.Requirement
import com.peter.letsswtich.data.User
import com.peter.letsswtich.dialog.MatchedDialogArgs
import com.peter.letsswtich.factory.ChatRoomViewModelFactory
import com.peter.letsswtich.factory.MatchedDialogViewModelFactory
import com.peter.letsswtich.factory.RequirementViewModelFactory
import com.peter.letsswtich.factory.ViewModelFactory

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as LetsSwtichApplication).letsSwitchRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(userEmail: String, userName: String) : ChatRoomViewModelFactory {
    val repository = (requireContext().applicationContext as LetsSwtichApplication).letsSwitchRepository
    return ChatRoomViewModelFactory(repository, userEmail, userName)
}

fun Fragment.getVmFactory(userRequiremnet: Requirement) : RequirementViewModelFactory {
    val repository = (requireContext().applicationContext as LetsSwtichApplication).letsSwitchRepository
    return RequirementViewModelFactory(repository, userRequiremnet)
}

fun Fragment.getVmFactory(matchedUser: User) : MatchedDialogViewModelFactory {
    val repository = (requireContext().applicationContext as LetsSwtichApplication).letsSwitchRepository
    return MatchedDialogViewModelFactory(repository, matchedUser)
}
