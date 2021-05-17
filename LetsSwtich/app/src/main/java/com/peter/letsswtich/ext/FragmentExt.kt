package com.peter.letsswtich.ext


import UserEmailViewModelFactory
import androidx.fragment.app.Fragment
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.factory.ViewModelFactory

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as LetsSwtichApplication).letsSwitchRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(userEmail: String, userName: String) : UserEmailViewModelFactory {
    val repository = (requireContext().applicationContext as LetsSwtichApplication).letsSwitchRepository
    return UserEmailViewModelFactory(repository, userEmail, userName)
}