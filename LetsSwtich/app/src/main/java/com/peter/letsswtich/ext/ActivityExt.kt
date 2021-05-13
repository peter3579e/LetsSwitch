package com.peter.letsswtich.ext

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.factory.ViewModelFactory

fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as LetsSwtichApplication).letsSwitchRepository
    return ViewModelFactory(repository)
}

fun Activity?.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}