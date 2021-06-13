package com.peter.letsswtich.data.source

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.peter.letsswtich.data.*

interface LetsSwitchDataSource {

    suspend fun getAllUser(): Result<List<User>>
    fun  getLiveChatList(myEmail: String): MutableLiveData<List<ChatRoom>>
    fun getAllLiveMessage(emails: List<String>): MutableLiveData<MessageWithId>
    fun getLiveEvent(): MutableLiveData<List<Events>>
    suspend fun getMessageItem(): List<Message>
    suspend fun getLikeList(myEmail: String, user: User): Result<List<String>>
    fun getNewMatchListener(myEmail: String): MutableLiveData<List<User>>
    suspend fun getUserDetail(userEmail:String): Result<User>
    suspend fun getMyOldMatchList(myEmail: String): Result<List<User>>
    suspend fun postUser(user: User): Result<Boolean>
    suspend fun updateUser (user: User): Result<Boolean>
    suspend fun removeFromChatList(myEmail: String,friendEmail: String): Result<Boolean>
    suspend fun postMessage(emails: List<String>, message: Message): Result<Boolean>
    suspend fun postChatRoom(chatRoom: ChatRoom): Result<Boolean>
    suspend fun updateIsRead(friendEmail:String,documentId: String):Result<Boolean>
    suspend fun updateMyLike(myEmail: String, user: User): Result<Boolean>
    suspend fun postEvent(events: Events): Result<Boolean>
    suspend fun postRequirement (myEmail: String, require: Requirement): Result<Boolean>
    suspend fun postmyLocation(longitude:Double,latitude:Double,myEmail: String): Result<Boolean>
    suspend fun getRequirement (myEmail: String): Result<Requirement>
    suspend fun updateMatch(myEmail: String, user: User): Result<Boolean>
    suspend fun removeUserFromLikeList(myEmail: String, user: User): Result<Boolean>
    suspend fun firebaseAuthWithGoogle(account : GoogleSignInAccount?): Result<FirebaseUser>
    suspend fun postfake()


}