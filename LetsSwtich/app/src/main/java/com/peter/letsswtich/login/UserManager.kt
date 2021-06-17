package com.peter.letsswtich.login

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.MainViewModel
import com.peter.letsswtich.data.User

const val tagUserUid = "uid"
const val tagUserToken = "token"

object UserManager {
    var user = User()
    var prefs: SharedPreferences? =
        LetsSwtichApplication.instance?.getSharedPreferences(tagUserToken, 0)
    var uid: String? = null
        get() {
            return prefs?.getString(
                tagUserUid, ""
            )
        }
        set(value) {
            field = prefs?.edit()?.putString(
                tagUserUid, value
            )?.apply().toString()
            Log.i("UserManager.Uid", value!!)
        }
}