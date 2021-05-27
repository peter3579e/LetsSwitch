package com.peter.letsswtich.data.source.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.R
import com.peter.letsswtich.data.*
import com.peter.letsswtich.data.source.LetsSwitchDataSource
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.util.Logger
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object LetsSwitchRemoteDataSource : LetsSwitchDataSource {

    private const val PATH_USER = "user"
    private const val PATH_MATCHLIST = "matchedList"
    private const val PATH_FOLLOWLIST = "followList"
    private const val PATH_CHATLIST = "chatList"
    private const val PATH_MATCHTIME= "matchedTime"


//    override suspend fun getChatList(): List<ChatRoom> {
//        var mock = mutableListOf<ChatRoom>()
//        mock.run {
//            add(
//                    ChatRoom(
//                            "123",
//                            1620355603699, listOf(UserInfo("peter7788@gmail.com", "Wency", "https://api.appworks-school.tw/assets/201807242228/main.jpg")),
//                            listOf("Peter", "Wency"), "Hello How are you doing?"
//                    )
//            )
//
//            add(
//                    ChatRoom(
//                            "123",
//                            1620355603699, listOf(UserInfo("peter3434@gmail.com", "Chloe", "https://api.appworks-school.tw/assets/201807202150/main.jpg")),
//                            listOf("Peter", "Chloe"), "Hello How are you doing?"
//                    )
//            )
//
//            add(
//                    ChatRoom(
//                            "123",
//                            1620355603699, listOf(UserInfo("peter123@gmail.com", "Gillan", "https://api.appworks-school.tw/assets/201807201824/main.jpg")),
//                            listOf("Peter", "Gillan"), "Hello How are you doing?"
//                    )
//            )
//
//        }
//        return mock
//    }

    override fun getLiveChatList(myEmail: String): MutableLiveData<List<ChatRoom>> {
        val liveData = MutableLiveData<List<ChatRoom>>()
        FirebaseFirestore.getInstance()
                .collection(PATH_CHATLIST)
                .orderBy("latestTime", Query.Direction.DESCENDING)
                .whereArrayContains("attendees", myEmail)
                .addSnapshotListener { snapshot, exception ->
                    Logger.i("add SnapshotListener detected")

                    exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }

                    val list = mutableListOf<ChatRoom>()
                    snapshot?.forEach { document ->
                        Logger.d(document.id + " => " + document.data)

                        val chatRoom = document.toObject(ChatRoom::class.java)
                        list.add(chatRoom)
                    }
                    liveData.value = list
                    Log.d("RemotedateSource","value of getLiveChatList =${liveData.value!!.size}")

                }
        return liveData
    }


    override suspend fun postChatRoom(chatRoom: ChatRoom): Result<Boolean> = suspendCoroutine { continuation ->
        val chat = FirebaseFirestore.getInstance().collection(PATH_CHATLIST)
        val document = chat.document()

        chatRoom.chatRoomId = document.id
        chatRoom.latestTime = Calendar.getInstance().timeInMillis

        chat.whereIn("attendees", listOf(chatRoom.attendees, chatRoom.attendees.reversed()))
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        document
                                .set(chatRoom)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Logger.i("Chatroom: $chatRoom")

                                        continuation.resume(Result.Success(true))
                                    } else {
                                        task.exception?.let {

                                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                                            continuation.resume(Result.Error(it))
                                            return@addOnCompleteListener
                                        }
                                        continuation.resume(Result.Fail(LetsSwtichApplication.appContext.getString(R.string.you_shall_not_pass)))
                                    }
                                }
                    } else {
                        for (myDocument in result) {
                            Logger.d("Already initialized")
                        }
                    }
                }

    }

    override suspend fun postMessage(emails: List<String>, message: Message): Result<Boolean> = suspendCoroutine { continuation ->

        val chat = FirebaseFirestore.getInstance().collection(PATH_CHATLIST)
        chat.whereIn("attendees", listOf(emails, emails.reversed()))
                .get()
                .addOnSuccessListener { result ->
                    val documentId = chat.document(result.documents[0].id)
                    documentId
                            .update("latestMessageTime", Calendar.getInstance().timeInMillis, "latestMessage", message.text)
                }
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        if (task.exception != null) {
                            task.exception?.let {
                                Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                                continuation.resume(Result.Error(it))
                            }
                        } else {
                            continuation.resume(Result.Fail(LetsSwtichApplication.appContext.getString(R.string.you_shall_not_pass)))
                        }
                    }

                    task.result?.let {
                        val documentId2 = chat.document(it.documents[0].id).collection("message").document()

                        message.createdTime = Calendar.getInstance().timeInMillis
                        message.id = documentId2.id

                        chat.document(it.documents[0].id).collection("message").add(message)

                    }


                }
                .addOnCompleteListener { taskTwo ->
                    if (taskTwo.isSuccessful) {
                        Logger.i("Chatroom: $message")

                        continuation.resume(Result.Success(true))
                    } else {
                        taskTwo.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(LetsSwtichApplication.appContext.getString(R.string.you_shall_not_pass)))
                    }

                }

    }


    override fun getAllLiveMessage(emails: List<String>): MutableLiveData<List<Message>> {
        val liveData = MutableLiveData<List<Message>>()

        val chat = FirebaseFirestore.getInstance().collection(PATH_CHATLIST)
        chat.whereIn("attendees", listOf(emails, emails.reversed()))
                .get()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            return@addOnCompleteListener
                        }
                    }

                    task.result?.let {
                        chat.document(it.documents[0].id).collection("message")

                                .orderBy("createdTime", Query.Direction.ASCENDING)
                                .addSnapshotListener { snapshot, exception ->
                                    Logger.i("add SnapshotListener detected")

                                    exception?.let {
                                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                                    }

                                    val list = mutableListOf<Message>()
                                    snapshot?.forEach { document ->
                                        Logger.d(document.id + " => " + document.data)

                                        val message = document.toObject(Message::class.java)
                                        list.add(message)
                                    }
                                    liveData.value = list

                                }

                    }

                }
        return liveData


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

                        continuation.resume(Result.Success(list))
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
                        continuation.resume(Result.Success(list))
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

                        continuation.resume(Result.Success(list))
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
                .orderBy("matchTime", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, exception ->
                    Logger.i("add SnapshotListener detected")

                    exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }
                    val list = mutableListOf<User>()
                    if (snapshot != null) {
                        for (document in snapshot) {
                            Log.d("remoteDataSource","getNewMatchListener has run !!!")
//                            Logger.d(document.id + " => " + document.data)

                            val event = document.toObject(User::class.java)
                            Log.d("testtest","matchTime value = ${event.matchTime}")
                            list.add(event)
                        }
                    }
                    livedData.value = list
                }

        return livedData
    }





    override suspend fun updateMyLike(myEmail: String, user: User): Result<Boolean> = suspendCoroutine {
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


    override suspend fun updateMatch(myEmail: String,user: User): Result<Boolean> = suspendCoroutine {



        val users = FirebaseFirestore.getInstance().collection(PATH_USER)
        val matchList = users.document(user.email).collection(PATH_MATCHLIST).document(myEmail)
                user.matchTime = Calendar.getInstance().timeInMillis
        matchList.set(user)
                .addOnSuccessListener {
                    Logger.d("DocumentSnapshot added with ID: ${users}")
                }
                .addOnFailureListener { e ->
                    Logger.w("Error adding document $e")
                }
        val myMatch = users.document(myEmail).collection(PATH_MATCHLIST).document(user.email)
                myMatch.set(user)
                .addOnSuccessListener {
                    Logger.d("DocumentSnapshot added with ID: ${users}")
                }
                .addOnFailureListener { e ->
                    Logger.w("Error adding document $e")
                }

    }


    override suspend fun removeUserFromLikeList(myEmail: String, user: User): Result<Boolean> = suspendCoroutine { continuation ->
        val users = FirebaseFirestore.getInstance().collection(PATH_USER)
        users.document(myEmail).collection(PATH_FOLLOWLIST).document(user.email)
                .delete()
                .addOnSuccessListener {
                    Logger.d("DocumentSnapshot added with ID: ${users}")
                }
                .addOnFailureListener { e ->
                    Logger.w("Error adding document $e")
                }
        users.document(myEmail).collection(PATH_MATCHLIST).document(user.email)
                .delete()
                .addOnSuccessListener {
                    Logger.d("DocumentSnapshot added with ID: ${users}")
                }
                .addOnFailureListener { e ->
                    Logger.w("Error adding document $e")
                }
        users.document(user.email).collection(PATH_MATCHLIST).document(myEmail)
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
                "googleId" to "12345",
                "age" to 25,
                "latitude" to 25.034070787981246,
                "lngti" to 121.53106153460475,
                "gender" to "Male",
                "likeList" to listOf("jsidfjisdjfiasf", "sfdasdfasdf"),
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