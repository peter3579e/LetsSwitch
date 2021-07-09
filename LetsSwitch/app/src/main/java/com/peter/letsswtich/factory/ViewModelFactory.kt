package com.peter.letsswtich.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.MainViewModel
import com.peter.letsswtich.chat.ChatViewModel
import com.peter.letsswtich.chatroom.ChatRoomViewModel
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.editprofile.EditViewModel
import com.peter.letsswtich.editprofile.preview.PreviewViewModel
import com.peter.letsswtich.home.HomeViewModel
import com.peter.letsswtich.login.LoginActivityViewModel
import com.peter.letsswtich.login.LoginViewModel
import com.peter.letsswtich.map.MapViewModel
import com.peter.letsswtich.map.event.EditEventViewModel
import com.peter.letsswtich.question.FirstQuestionnaireViewModel
import com.peter.letsswtich.question.SecondQuestionnaireViewModel
import com.peter.letsswtich.setting.SettingViewModel
import kotlin.math.E

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val letsSwitchRepository: LetsSwitchRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(letsSwitchRepository)

                isAssignableFrom(FirstQuestionnaireViewModel::class.java) ->
                    FirstQuestionnaireViewModel(letsSwitchRepository)

                isAssignableFrom(ChatViewModel::class.java) ->
                    ChatViewModel(letsSwitchRepository)

                isAssignableFrom(MapViewModel::class.java) ->
                    MapViewModel(letsSwitchRepository)

                isAssignableFrom(EditViewModel::class.java) ->
                    EditViewModel(letsSwitchRepository)

                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(letsSwitchRepository)

                isAssignableFrom(PreviewViewModel::class.java) ->
                    PreviewViewModel(letsSwitchRepository)

                isAssignableFrom(SecondQuestionnaireViewModel::class.java) ->
                    SecondQuestionnaireViewModel(letsSwitchRepository)

                isAssignableFrom(LoginActivityViewModel::class.java) ->
                    LoginActivityViewModel(letsSwitchRepository)

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(letsSwitchRepository)

                isAssignableFrom(EditEventViewModel::class.java) ->
                    EditEventViewModel(letsSwitchRepository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}