package com.peter.letsswtich.data.source.remote

import com.peter.letsswtich.data.Result
import com.peter.letsswtich.data.User
import com.peter.letsswtich.data.source.LetsSwitchDataSource

object LetsSwtichRemoteDataSource : LetsSwitchDataSource {

    override suspend fun getUserItem(): List<User> {
        var mock = mutableListOf<User>()
        mock.run {
            add(
                User(
                1,
                "Peter",
                "peter359js@yahoo.com.twpeter359js@yahoo.com.tw",
                arrayOf("https://api.appworks-school.tw/assets/201807242228/main.jpg",
                    "https://api.appworks-school.tw/assets/201807202150/main.jpg",
                    "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                    "https://api.appworks-school.tw/assets/201807201824/main.jpg"),
                    "https://api.appworks-school.tw/assets/201807202150/main.jpg",
                "Taipei",
                "Hawai",
                "Male",
                "I love you you love me",
                "Wizar"
            )
            )
            add(
                User(2,"Sean","peter359js@yahoo.com.twpeter359js@yahoo.com.tw",arrayOf("https://api.appworks-school.tw/assets/201807242228/main.jpg",
                "https://api.appworks-school.tw/assets/201807202150/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg"),
                    "https://api.appworks-school.tw/assets/201807202150/main.jpg",
                    "Taipei","Hawai","Male","I love you you love me","Wizar")
            )
            add(
                User(3,"Wency","peter359js@yahoo.com.twpeter359js@yahoo.com.tw",arrayOf("https://api.appworks-school.tw/assets/201807242228/main.jpg",
                "https://api.appworks-school.tw/assets/201807202150/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg"),
                    "https://api.appworks-school.tw/assets/201807202150/main.jpg","Taipei","Hawai","FeMale","I love you you love me","Wizar")
            )
            add(
                User(4,"Gillian","peter359js@yahoo.com.twpeter359js@yahoo.com.tw",arrayOf("https://api.appworks-school.tw/assets/201807242228/main.jpg",
                "https://api.appworks-school.tw/assets/201807202150/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg"),
                    "https://api.appworks-school.tw/assets/201807202150/main.jpg","Taipei","Hawai","FeMale","I love you you love me","Wizar")
            )
            add(
                User(5,"Chloe","peter359js@yahoo.com.twpeter359js@yahoo.com.tw",arrayOf("https://api.appworks-school.tw/assets/201807242228/main.jpg",
                "https://api.appworks-school.tw/assets/201807202150/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg"),
                    "https://api.appworks-school.tw/assets/201807202150/main.jpg","Taipei","Hawai","FeMale","I love you you love me","Wizar")
            )
            add(
                User(6,"Wayne","peter359js@yahoo.com.twpeter359js@yahoo.com.tw",arrayOf("https://api.appworks-school.tw/assets/201807242228/main.jpg",
                "https://api.appworks-school.tw/assets/201807202150/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg"),
                    "https://api.appworks-school.tw/assets/201807202150/main.jpg","Taipei","Hawai","Male","I love you you love me","Wizar")
            )
            add(
                User(7,"Marina","peter359js@yahoo.com.twpeter359js@yahoo.com.tw",arrayOf("https://api.appworks-school.tw/assets/201807242228/main.jpg",
                "https://api.appworks-school.tw/assets/201807202150/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg"),
                    "https://api.appworks-school.tw/assets/201807202150/main.jpg","Taipei","Hawai","FeMale","I love you you love me","Wizar")
            )
            add(
                User(8,"Thet","peter359js@yahoo.com.twpeter359js@yahoo.com.tw",arrayOf("https://api.appworks-school.tw/assets/201807242228/main.jpg",
                "https://api.appworks-school.tw/assets/201807202150/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg"),
                    "https://api.appworks-school.tw/assets/201807202150/main.jpg","Taipei","Hawai","FeMale","I love you you love me","Wizar")
            )
        }
        return mock
    }
}