import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.chatroom.ChatRoomViewModel
import com.peter.letsswtich.data.source.LetsSwitchRepository

@Suppress("UNCHECKED_CAST")
class UserEmailViewModelFactory constructor(
    private val repository: LetsSwitchRepository,
    private val userEmail: String,
    private val userName: String
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(ChatRoomViewModel::class.java) ->
                    ChatRoomViewModel(repository, userEmail, userName)


                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}