package com.peter.letsswtich.data.source

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.peter.letsswtich.data.*


class DefaultLetsSwitchRepository(
    private val remoteDataSource: LetsSwitchDataSource,
    private val localDataSource: LetsSwitchDataSource
) :
    LetsSwitchRepository {

    override suspend fun getAllUser(): Result<List<User>> {
        return remoteDataSource.getAllUser()
    }

    override fun getLiveChatList(myEmail: String): MutableLiveData<List<ChatRoom>> {
        return remoteDataSource.getLiveChatList(myEmail)
    }

    override suspend fun getUserDetail(userEmail:String): Result<User>{
        return remoteDataSource.getUserDetail(userEmail)
    }

    override fun getAllLiveMessage(emails: List<String>): MutableLiveData<MessageWithId> {
        return remoteDataSource.getAllLiveMessage(emails)
    }

    override fun getLiveEvent(): MutableLiveData<List<Events>> {
        return remoteDataSource.getLiveEvent()
    }

    override fun getLiveJoinList(events: Events): MutableLiveData<List<User>>{
        return remoteDataSource.getLiveJoinList(events)
    }


    override suspend fun getMessageItem(): List<Message> {
        return remoteDataSource.getMessageItem()
    }

    override suspend fun getLikeList(myEmail: String, user: User): Result<List<String>> {
        return remoteDataSource.getLikeList(myEmail,user)
    }

    override fun getNewMatchListener(myEmail: String): MutableLiveData<List<User>> {
        return remoteDataSource.getNewMatchListener(myEmail)
    }

    override suspend fun getMyOldMatchList(myEmail: String): Result<List<User>> {
        return remoteDataSource.getMyOldMatchList(myEmail)
    }

    override suspend fun postMessage(emails: List<String>, message: Message): Result<Boolean> {
        return remoteDataSource.postMessage(emails, message)
    }

    override suspend fun postUser(user: User): Result<Boolean>{
        return remoteDataSource.postUser(user)
    }

    override suspend fun postJoin(myEmail: String, events: Events): Result<Boolean>{
        return remoteDataSource.postJoin(myEmail,events)
    }

    override suspend fun updateUser(user: User): Result<Boolean> {
        return remoteDataSource.updateUser(user)
    }

    override suspend fun postRequirement(myEmail: String, require: Requirement): Result<Boolean>{
        return remoteDataSource.postRequirement(myEmail,require)
    }

    override suspend fun removeFromChatList(myEmail: String,friendEmail: String):Result<Boolean>{
        return  remoteDataSource.removeFromChatList(myEmail,friendEmail)
    }

    override suspend fun postmyLocation(longitude:Double,latitude:Double,myEmail: String): Result<Boolean>{
        return remoteDataSource.postmyLocation(longitude,latitude,myEmail)
    }


    override suspend fun postChatRoom(chatRoom: ChatRoom): Result<Boolean> {
        return remoteDataSource.postChatRoom(chatRoom)
    }

    override suspend fun postEvent(events: Events): Result<Boolean> {
        return remoteDataSource.postEvent(events)
    }

    override suspend fun updateMyLike(myEmail: String, user: User): Result<Boolean>{
        return remoteDataSource.updateMyLike(myEmail,user)
    }

    override suspend fun updateIsRead(friendEmail:String,documentId: String): Result<Boolean>{
        return remoteDataSource.updateIsRead(friendEmail,documentId)
    }

    override suspend fun getRequirement(myEmail: String): Result<Requirement> {
        return remoteDataSource.getRequirement(myEmail)
    }

    override suspend fun updateMatch(myEmail: String, user: User): Result<Boolean> {
        return remoteDataSource.updateMatch(myEmail,user)}

    override suspend fun removeUserFromLikeList(myEmail: String, user: User): Result<Boolean>{
        return remoteDataSource.removeUserFromLikeList(myEmail,user)
    }

    override suspend fun firebaseAuthWithGoogle(account : GoogleSignInAccount?): Result<FirebaseUser> {
        return remoteDataSource.firebaseAuthWithGoogle(account)
    }

    override suspend fun postfake(){
        return remoteDataSource.postfake()
    }

}