package com.peter.letsswtich.data.source.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.*
import com.peter.letsswtich.data.source.LetsSwitchDataSource
import com.peter.letsswtich.util.Logger
import kotlin.Result
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object LetsSwitchRemoteDataSource : LetsSwitchDataSource {

    private const val PATH_USER = "user"


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


    override suspend fun getAllUser(): com.peter.letsswtich.data.Result<List<User>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USER)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val list = mutableListOf<User>()
                    task.result?.forEach { document ->
                        Logger.d(document.id + " => " + document.data)

                        val user = document.toObject(User::class.java)
                        list.add(user)
                        Log.d("LetsSwitch DataSource", "value of user $user")
                    }
                    continuation.resume(com.peter.letsswtich.data.Result.Success(list))
                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message} ")
                        continuation.resume(com.peter.letsswtich.data.Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(
                        com.peter.letsswtich.data.Result.Fail(
                            LetsSwtichApplication.appContext.getString(
                                R.string.get_nothing_from_firebase)))
                }
            }

    }

    override suspend fun postUser(){
        val user = FirebaseFirestore.getInstance().collection(PATH_USER)
        val document = user.document()
        val data = hashMapOf(
            "personImages" to listOf("https://api.appworks-school.tw/assets/201807242228/main.jpg",
                "https://api.appworks-school.tw/assets/201807202150/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                "https://api.appworks-school.tw/assets/201807201824/main.jpg"),
            "description" to "I love playing tennis",
            "status" to "boring",
            "id" to "123",
            "bigheadPic" to "https://api.appworks-school.tw/assets/201807201824/main.jpg",
            "googleId" to "12345",
            "age" to 26,
            "latitude" to 25.034070787981246,
            "lngti" to 121.53106153460475,
            "gender" to "Male",
            "likeList" to listOf("jsidfjisdjfiasf","sfdasdfasdf"),
            "dislikeList" to listOf("Sdfasdf","sdfasf"),
            "likedFromUser" to listOf("sdfasdfasdf","sdfasdfdfs"),
            "friends" to listOf("sadfadfadfadsf","asdfadsfasdf"),
            "name" to "Peter Liu",
            "email" to "peter324234@yahoo.com.tw",
            "city" to "Taipei",
            "district" to "Hawai",
            "role" to "teacher"
        )
        Log.d("Peter","The post has run")

        document.set(data)
            .addOnSuccessListener { Log.d("Update", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("Update", "Error writing document", e) }
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
                StoreLocation(Store("123","Wayne","https://api.appworks-school.tw/assets/201807242228/main.jpg"),"想找人約跑步",25.034070787981246, 121.53106153460475,"0938941285")
            )
            add(
                StoreLocation(Store("123","Chloe","https://api.appworks-school.tw/assets/201807201824/main.jpg"),"想吃飯",22.99095476570537, 120.19685561482974,"0938941285")
            )
            add(
                StoreLocation(Store("123","Scolly","https://api.appworks-school.tw/assets/201807202150/main.jpg"),"想看電影",25.034658371107255, 121.53197895144412,"0938941285")
            )
        }
        return mock
    }
}