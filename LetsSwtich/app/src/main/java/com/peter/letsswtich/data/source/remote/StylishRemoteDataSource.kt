package com.peter.letsswtich.data.source.remote

import com.peter.letsswtich.data.*
import com.peter.letsswtich.data.source.LetsSwitchDataSource

object LetsSwtichRemoteDataSource : LetsSwitchDataSource {


    override suspend fun getChatItem(): List<ChatRoom> {
        var mock = mutableListOf<ChatRoom>()
        mock.run {
            add(
                ChatRoom(
                "123",
                1620355603699, listOf(UserInfo("peter7788@gmail.com","Wency", "https://api.appworks-school.tw/assets/201807242228/main.jpg")),
                    listOf("Peter","Wency"),"Hello How are you doing?"
            )
            )

            add(
                ChatRoom(
                    "123",
                    1620355603699, listOf(UserInfo("peter3434@gmail.com","Chloe", "https://api.appworks-school.tw/assets/201807202150/main.jpg")),
                    listOf("Peter","Chloe"),"Hello How are you doing?"
                )
            )

            add(
                ChatRoom(
                    "123",
                    1620355603699, listOf(UserInfo("peter123@gmail.com","Gillan", "https://api.appworks-school.tw/assets/201807201824/main.jpg")),
                    listOf("Peter","Gillan"),"Hello How are you doing?"
                )
            )

        }
        return mock
    }


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

    override suspend fun getMessageItem(): List<Message>{
        var mock = mutableListOf<Message>()
        mock.run{
            add(
                Message("123","Wency","https://api.appworks-school.tw/assets/201807242228/main.jpg","peter7788@gmail.com","雪莉？", 1620355603699)
            )
            add(
                    Message("123","Peter","https://api.appworks-school.tw/assets/201807201824/main.jpg","peter3579e@gmail.com","哇嗚珍妮佛羅茲！！！！！", 1620355603699)
            )
            add(
                    Message("123","Wency","https://api.appworks-school.tw/assets/201807242228/main.jpg","peter7788@gmail.com","你為什麼要攻擊我的coin master村莊？", 1620355603699)
            )
        }
        return mock
    }

    override suspend fun getMapItem(): List<StoreLocation>{
        var mock = mutableListOf<StoreLocation>()
        mock.run{
            add(
                StoreLocation(Store("123","AppleStore","https://api.appworks-school.tw/assets/201807242228/main.jpg"),"奇福扁食",25.034070787981246, 121.53106153460475,"0938941285")
            )
        }
        return mock
    }
}