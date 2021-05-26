package com.peter.letsswtich.ext


import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.data.Requirement
import com.peter.letsswtich.data.User
import com.peter.letsswtich.login.UserManager


fun List<User>?.excludeUser() : List<User> {
    return this?.filter {
        it?.let {
            it.email != UserManager.user.email
        }
    }
            ?: listOf()
}

fun List<User>?.filterByTraits(requirement: Requirement) : List<User> {
    return this?.filter {
        it?.let {
            if(requirement.gender == ""){
                it.city == requirement.city  && it.fluentLanguage.contains(requirement.language) && it.city ==
                        requirement.city && it.email != UserManager.user.email && it.age < requirement.age[1] && requirement.age[0] < it.age
            } else{
                it.city == requirement.city && it.gender == requirement.gender && it.fluentLanguage.contains(requirement.language) &&
                        it.age < requirement.age[1] && requirement.age[0] < it.age && it.email != UserManager.user.email
            }

        }
    }
            ?: listOf()
}

fun List<ChatRoom>?.sortByTimeStamp (selectedTime: Long) : List<ChatRoom>{

    return this?.filter{
        it?.let {
            selectedTime <= it.latestMessageTime && it.latestMessageTime < selectedTime + 86400000
        }

    }
            ?: listOf()

}

