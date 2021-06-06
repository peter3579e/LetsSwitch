package com.peter.letsswtich.ext


import androidx.fragment.app.Fragment
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.data.Requirement
import com.peter.letsswtich.data.User
import com.peter.letsswtich.factory.*

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as LetsSwtichApplication).letsSwitchRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(userEmail: String, userName: String, fromMap: Boolean) : ChatRoomViewModelFactory {
    val repository = (requireContext().applicationContext as LetsSwtichApplication).letsSwitchRepository
    return ChatRoomViewModelFactory(repository, userEmail, userName, fromMap)
}

fun Fragment.getVmFactory(userRequiremnet: Requirement) : RequirementViewModelFactory {
    val repository = (requireContext().applicationContext as LetsSwtichApplication).letsSwitchRepository
    return RequirementViewModelFactory(repository, userRequiremnet)
}

fun Fragment.getVmFactory(matchedUser: User) : UserViewModelFactory {
    val repository = (requireContext().applicationContext as LetsSwtichApplication).letsSwitchRepository
    return UserViewModelFactory(repository, matchedUser)
}

fun Fragment.getVmFactory(matchedUser: User, fromMap: Boolean) : ProFileViewModelFactory {
    val repository = (requireContext().applicationContext as LetsSwtichApplication).letsSwitchRepository
    return ProFileViewModelFactory(repository, matchedUser, fromMap)
}
