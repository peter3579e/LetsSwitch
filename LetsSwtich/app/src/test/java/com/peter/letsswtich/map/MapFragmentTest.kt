package com.peter.letsswtich.map
import com.peter.letsswtich.data.User
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.peter.letsswtich.map.event.EditEventFragment

class MapFragmentTest {

    private val user1 = User(email = "123",age = 1)
    private val user2 = User(email = "234",age = 1)
    private val user3 = User(email = "345",age = 1)
    private val user4 = User(email = "9527",age = 1)
    private val matchList = listOf<User>(user1,user2,user3,user4)
    private val uri = "444"
    private val photoList = mutableListOf<String>("123","123","123","","","","","")


    @Test
    fun getUserEmail(){
        val map = MapFragment()
        val result = map.getUserEmails(matchList)
        assertThat(result).isEqualTo(listOf<String>(user1.email,user2.email,user3.email,user4.email))
    }

    @Test
    fun excludeEmptySpaces(){
        val event = EditEventFragment()
        val result = event.addPhoto(uri,photoList)
        assertThat(result).isNotEmpty()
        assertThat(result).isEqualTo(mutableListOf("123","123","123","444","","","",""))

    }
}