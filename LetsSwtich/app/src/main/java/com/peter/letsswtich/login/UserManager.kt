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

//            User(
//    listOf("https://img.onl/3zX6",
//    "https://api.appworks-school.tw/assets/201807202150/main.jpg",
//    "https://api.appworks-school.tw/assets/201807201824/main.jpg",
//    "https://api.appworks-school.tw/assets/201807201824/main.jpg"),
//    "I love playing tennis and basketball",
//    "Feeling Bored...",
//    "123",
//    "12345",
//    25,
//    25.034070787981246,
//    121.53106153460475,
//    "Male",
//    "Peter Liu",
//    "peter3579e@gmail.com",
//    "Taipei",
//    "Hawai",
//    listOf("jsidfjisdjfiasf","sfdasdfasdf"),
//    "Teacher",
//    listOf("English","Chinese"),
//    listOf("Japanese","French"),
//    "https://img.onl/iiFJIS",
//    0L
//
//
//    )

    var prefs : SharedPreferences? = LetsSwtichApplication.instance?.getSharedPreferences(tagUserToken, 0)

    var uid : String? = null
        get(){
            return prefs?.getString(
                tagUserUid, "")
        }
        set(value){
            field = prefs?.edit()?.putString(
                tagUserUid,value)?.apply().toString()
            Log.i("UserManager.Uid", value!!)
        }




}