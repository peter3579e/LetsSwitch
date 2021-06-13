package com.peter.letsswtich.data.source.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentId
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
    private const val PATH_REQUIREMENT = "requirement"
    private const val PATH_EVENT = "events"
    private const val TAG = "letsSwitchRemoteDataSource"


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


    override suspend fun postEvent(events: Events): Result<Boolean> = suspendCoroutine { continuation ->

        Log.d(TAG,"the value of post events = $events")
        Log.d(TAG,"Run post event 1")

        val event = FirebaseFirestore.getInstance().collection(PATH_EVENT)

        Log.d(TAG,"Run post event 2")
        val document = event.document()

        Log.d(TAG,"Run post event 3")

        events.eventId = document.id
        Log.d(TAG,"Run post event 4")
        events.postTime = Calendar.getInstance().timeInMillis

        Log.d(TAG,"Run post event 5")
        document.set(events)
                .addOnSuccessListener {
                    Log.d(TAG,"Run post event 6")
                    Logger.d("DocumentSnapshot added with ID: ${event}")
                }
                .addOnFailureListener { e ->
                    Log.d(TAG,"Run post event 7")
                    Logger.w("Error adding document $e")
                }


    }

    override fun getLiveEvent(): MutableLiveData<List<Events>> {
        val liveData = MutableLiveData<List<Events>>()
        FirebaseFirestore.getInstance()
                .collection(PATH_EVENT)
                .orderBy("postTime", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, exception ->
                    Logger.i("add SnapshotListener detected")

                    exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }

                    val list = mutableListOf<Events>()
                    snapshot?.forEach { document ->
                        Logger.d(document.id + " => " + document.data)

                        val eventList = document.toObject(Events::class.java)
                        list.add(eventList)
                    }
                    liveData.value = list
                    Log.d("RemotedateSource","value of getLiveEvent =${liveData.value!!.size}")

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

    override suspend fun updateIsRead(friendEmail:String,documentId: String):Result<Boolean> = suspendCoroutine { continuation ->
        val chat = FirebaseFirestore.getInstance().collection(PATH_CHATLIST).document(documentId)
//        Log.d("RemoteDataSource","updateIsRead has run")
        chat.collection("message")
            .whereEqualTo("senderEmail",friendEmail)
            .get()
            .addOnCompleteListener { task ->
//                Log.d("RemoteDataSource","updateIsRead has run1")
                if (!task.isSuccessful) {
//                    Log.d("RemoteDataSource","updateIsRead has run2")
                    if (task.exception != null) {
//                        Log.d("RemoteDataSource","updateIsRead has run3")
                        task.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                    } else {
//                        Log.d("RemoteDataSource","updateIsRead has run4")
                        continuation.resume(Result.Fail(LetsSwtichApplication.appContext.getString(R.string.you_shall_not_pass)))
                    }
                }
                task.result?.let {
                    Log.d("RemoteDataSource","updateIsRead has run5")
                    Log.d("RemoteDataSource","task result value = ${task.result}")
                    for (document in it){
                        chat.collection("message").document(document.id).update("read",true)
                    }
                }
            }
    }

    override suspend fun removeFromChatList(myEmail:String, friendEmail: String): Result<Boolean> = suspendCoroutine { continuation ->
        Log.d("removeFromChatList","my email value = $myEmail")
        Log.d("removeFromChatList","friend email value = $friendEmail")
        Log.d("removeUserFromChatList","Run1")
        var listUser = listOf<String>(myEmail,friendEmail)
        val chat = FirebaseFirestore.getInstance().collection(PATH_CHATLIST)
        Log.d("removeUserFromChatList","Run2")
        chat.whereIn("attendees", listOf(listUser,listUser.reversed()))
                .get()

                .addOnSuccessListener { result ->
                    Log.d("removeUserFromChatList","Run3")
                    for (document in result){
                        val documentId = chat.document(document.id)
                        documentId.delete()
                    }
                    Log.d("removeUserFromChatList","Run5")
                }
                .addOnFailureListener { e ->
                    Logger.w("Error adding document $e")
                }
    }

    override suspend fun postMessage(emails: List<String>, message: Message): Result<Boolean> = suspendCoroutine { continuation ->
        Log.d("postMessage","the value of my message = $emails")
        Log.d("postMessage","the value of friend message = ${emails.reversed()}")
        val chat = FirebaseFirestore.getInstance().collection(PATH_CHATLIST)
        chat.whereIn("attendees", listOf(emails, emails.reversed()))
            .get()
            .addOnSuccessListener { result ->
                Log.d("the value of result","result = $result")
                val documentId = chat.document(result.documents[0].id)
                Log.d("PostMessage","The value of PostMessage = $documentId")
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


    override fun getAllLiveMessage(emails: List<String>): MutableLiveData<MessageWithId> {
        val liveData = MutableLiveData<MessageWithId>()

        val chat = FirebaseFirestore.getInstance().collection(PATH_CHATLIST)
        chat.whereIn("attendees", listOf(emails, emails.reversed()))
                .get()
                .addOnCompleteListener { task ->
                    var documentId =""
                    Log.d("RemotedataSource","getAllLiveMessage Run1")
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            return@addOnCompleteListener
                        }
                    }
                    task.result?.forEach { document ->

                        Log.d("RemotedataSource","getAllLiveMessage Run2")

                        Logger.d(document.id + " => " + document.data)

                        val id = document.id

                        documentId = id

                        Log.d("RemoteDataSource","value of documentId = $documentId ")

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
                                    liveData.value = MessageWithId(list,documentId)

                                }

                    }

                }


        return liveData



    }



    override suspend fun getRequirement (myEmail: String): Result<Requirement> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
                .collection(PATH_USER)
                .document(myEmail)
                .collection(PATH_REQUIREMENT)
                .document(myEmail)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var list = Requirement()

                        task.result?.let {
                            val needs = it.toObject(Requirement::class.java)

                            if (needs != null) {
                                list = needs
                            }
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

    override suspend fun getUserDetail(userEmail:String):Result<User> = suspendCoroutine { continuation ->

        val user = FirebaseFirestore.getInstance().collection(PATH_USER)
        user.whereEqualTo("email",userEmail)
            .get()
            .addOnCompleteListener { task ->
                var userDetail = User()
                if(task.isSuccessful){

                    task.result?.forEach { document ->
                        Logger.d(document.id + " => " + document.data)

                        val user = document.toObject(User::class.java)

                        userDetail = user
                    }

                    continuation.resume(Result.Success(userDetail))
                }else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(LetsSwtichApplication.appContext.getString(R.string.you_shall_not_pass)))
                }
            }
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
//                            Log.d("testtest","matchTime value = ${event.matchTime}")
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
//        user.matchTime = Calendar.getInstance().timeInMillis


        val users = FirebaseFirestore.getInstance().collection(PATH_USER)

        //update matchList for friends
        val matchList = users.document(user.email).collection(PATH_MATCHLIST).document(myEmail)

        UserManager.user.matchTime = Calendar.getInstance().timeInMillis

        Log.d("user matchTime","mathc time value = ${UserManager.user.matchTime}")
        matchList.set(UserManager.user)
                .addOnSuccessListener {
                    Logger.d("DocumentSnapshot added with ID: ${users}")
                }
                .addOnFailureListener { e ->
                    Logger.w("Error adding document $e")
                }

        //update my matchList
        val myMatch = users.document(myEmail).collection(PATH_MATCHLIST).document(user.email)
        user.matchTime = Calendar.getInstance().timeInMillis
        Log.d("user matchTime","mathc time value = ${user.matchTime}")
                myMatch.set(user)
                .addOnSuccessListener {
                    Logger.d("DocumentSnapshot added with ID: ${users}")
                }
                .addOnFailureListener { e ->
                    Logger.w("Error adding document $e")
                }

    }

    override suspend fun postRequirement (myEmail: String, require: Requirement): Result<Boolean> = suspendCoroutine {
//        user.matchTime = Calendar.getInstance().timeInMillis


        val users = FirebaseFirestore.getInstance().collection(PATH_USER)

        //update matchList for friends
        val requirement = users.document(myEmail).collection(PATH_REQUIREMENT).document(myEmail)

        requirement.set(require)
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


    override suspend fun postfake() {
        val user = FirebaseFirestore.getInstance().collection(PATH_USER)
        val document = user.document("shireny@gmail.com")
        val data = hashMapOf(
                "personImages" to listOf("https://img.onl/CHR1wx",
                        "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                        "https://api.appworks-school.tw/assets/201807201824/main.jpg",
                        "https://api.appworks-school.tw/assets/201807201824/main.jpg"),
                "description" to "想看電影",
                "status" to "Busy",
                "id" to "123",
                "googleId" to "12345",
                "age" to 22,
                "latitude" to 25.034070787981246,
                "lngti" to 121.53106153460475,
                "gender" to "Female",
                "likeList" to listOf("jsidfjisdjfiasf", "sfdasdfasdf"),
                "name" to "shireny",
                "email" to "shireny@gmail.com",
                "city" to "Taipei",
                "district" to "Hawai",
            "backGroundPic" to "https://img.onl/ah0ION",
                "role" to "teacher",
                "fluentLanguage" to listOf("English", "Chinese"),
                "preferLanguage" to listOf("Japanese", "French")
        )
        Log.d("Peter", "The post has run")
        document.set(data)
                .addOnSuccessListener { Log.d("Update", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("Update", "Error writing document", e) }
    }


    override suspend fun postUser(user: User): Result<Boolean> = suspendCoroutine { continuation ->

        val users = FirebaseFirestore.getInstance().collection(PATH_USER)
        val document = users.document(user.email)
        user.id = document.id
//        user.joinedTime = Calendar.getInstance().timeInMillis

        users.whereEqualTo("email", user.email)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    document
                        .set(user)
                        .addOnSuccessListener {
                            Logger.d("DocumentSnapshot added with ID: ${users}")
                        }
                        .addOnFailureListener { e ->
                            Logger.w("Error adding document $e")
                        }
                } else {
                    for (myDocument in result) {
                        Logger.d("Already initialized")
                    }
                }
            }


    }

    override suspend fun postmyLocation(longitude:Double,latitude:Double,myEmail: String): Result<Boolean> = suspendCoroutine {
        val users = FirebaseFirestore.getInstance().collection(PATH_USER)
        users.document(myEmail)
                .update("lngti",longitude)
                .addOnCanceledListener {
                    Logger.d("DocumentSnapshot added with ID: ${users}")
                }
                .addOnFailureListener { e ->
                    Logger.w("Error adding document $e")
                }
        users.document(myEmail)
                .update("latitude",latitude)
                .addOnCanceledListener {
                    Logger.d("DocumentSnapshot added with ID: ${users}")
                }
                .addOnFailureListener { e ->
                    Logger.w("Error adding document $e")
                }
    }

    override suspend fun updateUser(user: User): Result<Boolean> = suspendCoroutine {
        val users = FirebaseFirestore.getInstance().collection(PATH_USER)
        users.document(user.email)
            .update("description", user.description,
                "backGroundPic", user.backGroundPic,
                "city", user.city,
                "district", user.district,
                "gender", user.gender,
                "fluentLanguage", user.fluentLanguage,
                "preferLanguage", user.preferLanguage,
            "status",user.status,
            "personImages", user.personImages)
            .addOnSuccessListener {
                Logger.d("DocumentSnapshot added with ID: ${users}")
            }
            .addOnFailureListener { e ->
                Logger.w("Error adding document $e")
            }

    }

    override suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount?): Result<FirebaseUser> = suspendCoroutine { continuation ->
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        val auth = FirebaseAuth.getInstance()
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("Post: $credential")
                    task.result?.let {
                        continuation.resume(Result.Success(it.user!!))
                    }
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents.")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(LetsSwtichApplication.instance.getString(R.string.you_shall_not_pass)))
                }
            }
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

}