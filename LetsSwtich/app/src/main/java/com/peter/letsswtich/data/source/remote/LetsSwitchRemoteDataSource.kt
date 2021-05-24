package com.peter.letsswtich.data.source.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.*
import com.peter.letsswtich.data.source.LetsSwitchDataSource
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.util.Logger
import kotlin.Result
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object LetsSwitchRemoteDataSource : LetsSwitchDataSource {

    private const val PATH_USER = "user"
    private const val PATH_MATCHLIST = "matchedList"
    private const val PATH_FOLLOWLIST = "followList"


    override suspend fun getChatItem(): List<ChatRoom> {
        var mock = mutableListOf<ChatRoom>()
        mock.run {
            add(
                    ChatRoom(
                            "123",
                            1620355603699, listOf(UserInfo("peter7788@gmail.com", "Wency", "https://api.appworks-school.tw/assets/201807242228/main.jpg")),
                            listOf("Peter", "Wency"), "Hello How are you doing?"
                    )
            )

            add(
                    ChatRoom(
                            "123",
                            1620355603699, listOf(UserInfo("peter3434@gmail.com", "Chloe", "https://api.appworks-school.tw/assets/201807202150/main.jpg")),
                            listOf("Peter", "Chloe"), "Hello How are you doing?"
                    )
            )

            add(
                    ChatRoom(
                            "123",
                            1620355603699, listOf(UserInfo("peter123@gmail.com", "Gillan", "https://api.appworks-school.tw/assets/201807201824/main.jpg")),
                            listOf("Peter", "Gillan"), "Hello How are you doing?"
                    )
            )

        }
        return mock
    }

    override suspend fun getLikeList(myEmail: String, user: User): com.peter.letsswtich.data.Result<List<String>> = suspendCoroutine { continuation ->
        Log.d("letsSwitchRemoteDataSource", "getLikeList has run")

        val users = FirebaseFirestore.getInstance().collection(PATH_USER)


        users.document(user.email)
                .collection(PATH_FOLLOWLIST)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<String>()
                        task.result?.forEach { document ->
                            Log.d("letsSwitchRemoteDataSource", "${document.id} => ${document.data}")
                            list.add(document.id)

                        }

                        Log.d("letsSwitchRemoteDataSource", "value of list User = $list")

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

    override suspend fun getAllUser(): com.peter.letsswtich.data.Result<List<User>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
                .collection(PATH_USER)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
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

    override suspend fun getMyOldMatchList (myEmail: String):com.peter.letsswtich.data.Result<List<User>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
                .collection(PATH_USER)
                .document(myEmail)
                .collection(PATH_MATCHLIST)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<User>()
                        task.result?.forEach { document ->
                            Log.d("letsSwitchRemoteDataSource", "${document.id} => ${document.data}")

                            val user = document.toObject(User::class.java)
                            list.add(user)
                        }

                        Log.d("letsSwitchRemoteDataSource", "value of OldMatchList = $list")

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

    override fun getNewMatchListener(myEmail: String):MutableLiveData<List<User>> {
        val livedData = MutableLiveData<List<User>>()
        FirebaseFirestore.getInstance()
                .collection(PATH_USER)
                .document(myEmail)
                .collection(PATH_MATCHLIST)
                .addSnapshotListener { snapshot, exception ->
                    Logger.i("add SnapshotListener detected")

                    exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    val list = mutableListOf<User>()
                    if (snapshot != null) {
                        for (document in snapshot) {
                            Logger.d(document.id + " => " + document.data)

                            val event = document.toObject(User::class.java)
                            list.add(event)
                        }
                    }
                    livedData.value = list
                }

        return livedData
    }





    override suspend fun updateMyLike(myEmail: String, user: User): com.peter.letsswtich.data.Result<Boolean> = suspendCoroutine {
        val users = FirebaseFirestore.getInstance().collection(PATH_USER)

        Log.d("letsSwitchRemoteDataSource", "UpdateAndCheckLike has run")

        users.document(myEmail).collection(PATH_FOLLOWLIST).document(user.email)
                .set(user)
                .addOnSuccessListener {
                    Logger.d("DocumentSnapshot added with ID: ${users}")
                }
                .addOnFailureListener { e ->
                    Logger.w("Error adding document $e")
                }
        users.document(myEmail).update("likeList", FieldValue.arrayUnion(user.email))


    }

    override suspend fun updateMatch(myEmail: String,user: User): com.peter.letsswtich.data.Result<Boolean> = suspendCoroutine {

        val users = FirebaseFirestore.getInstance().collection(PATH_USER)
        users.document(user.email).collection(PATH_MATCHLIST).document(myEmail)
                .set(UserManager.user)
                .addOnSuccessListener {
                    Logger.d("DocumentSnapshot added with ID: ${users}")
                }
                .addOnFailureListener { e ->
                    Logger.w("Error adding document $e")
                }
    }



    override suspend fun removeUserFromLikeList(myEmail: String, user: User): com.peter.letsswtich.data.Result<Boolean> = suspendCoroutine { continuation ->
        val users = FirebaseFirestore.getInstance().collection(PATH_USER)
        users.document(myEmail).collection(PATH_FOLLOWLIST).document(user.email)
                .delete()
                .addOnSuccessListener {
                    Logger.d("DocumentSnapshot added with ID: ${users}")
                }
                .addOnFailureListener { e ->
                    Logger.w("Error adding document $e")
                }
    }


    override suspend fun postUser() {
        val user = FirebaseFirestore.getInstance().collection(PATH_USER)
        val document = user.document("peter3579e@gmail.com")
        val data = hashMapOf(
                "personImages" to listOf("https://api.appworks-school.tw/assets/201807242228/main.jpg",
                        "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                        "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                        "https://api.appworks-school.tw/assets/201807201824/main.jpg"),
                "description" to "Hello",
                "status" to "boring",
                "id" to "123",
                "bigheadPic" to "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                "googleId" to "12345",
                "age" to 25,
                "latitude" to 25.034070787981246,
                "lngti" to 121.53106153460475,
                "gender" to "Male",
                "likeList" to listOf("jsidfjisdjfiasf", "sfdasdfasdf"),
                "dislikeList" to listOf("Sdfasdf", "sdfasf"),
                "likedFromUser" to listOf("sdfasdfasdf", "sdfasdfdfs"),
                "friends" to listOf("sadfadfadfadsf", "asdfadsfasdf"),
                "name" to "Peter",
                "email" to "peter3579e@gmail.com",
                "city" to "Taipei",
                "district" to "Hawai",
                "role" to "teacher",
                "fluentLanguage" to listOf("English", "Chinese"),
                "preferLanguage" to listOf("Japanese", "French")
        )
        Log.d("Peter", "The post has run")




        document.set(data)
                .addOnSuccessListener { Log.d("Update", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("Update", "Error writing document", e) }
    }

    override suspend fun getMessageItem(): List<Message> {
        var mock = mutableListOf<Message>()
        mock.run {
            add(
                    Message("123", "Wency", "https://api.appworks-school.tw/assets/201807242228/main.jpg", "peter7788@gmail.com", "雪莉？", 1620355603699)
            )
            add(
                    Message("123", "Peter", "https://api.appworks-school.tw/assets/201807201824/main.jpg", "peter3579e@gmail.com", "哇嗚珍妮佛羅茲！！！！！", 1620355603699)
            )
            add(
                    Message("123", "Wency", "https://api.appworks-school.tw/assets/201807242228/main.jpg", "peter7788@gmail.com", "你為什麼要攻擊我的coin master村莊？", 1620355603699)
            )
        }
        return mock
    }

    override suspend fun getMapItem(): List<StoreLocation> {
        var mock = mutableListOf<StoreLocation>()
        mock.run {
            add(
                    StoreLocation(Store("123", "Wayne", "https://api.appworks-school.tw/assets/201807242228/main.jpg"), "想找人約跑步", 25.034070787981246, 121.53106153460475, "0938941285")
            )
            add(
                    StoreLocation(Store("123", "Chloe", "https://api.appworks-school.tw/assets/201807201824/main.jpg"), "想吃飯", 22.99095476570537, 120.19685561482974, "0938941285")
            )
            add(
                    StoreLocation(Store("123", "Scolly", "https://api.appworks-school.tw/assets/201807202150/main.jpg"), "想看電影", 25.034658371107255, 121.53197895144412, "0938941285")
            )
        }
        return mock
    }
}