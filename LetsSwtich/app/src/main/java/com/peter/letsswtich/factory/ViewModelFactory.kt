package com.peter.letsswtich.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.chat.ChatViewModel
import com.peter.letsswtich.chatroom.ChatRoomViewModel
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.home.HomeViewModel
import com.peter.letsswtich.map.MapViewModel
import com.peter.letsswtich.question.FirstQuestionnaireViewModel

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

                isAssignableFrom(FirstQuestionnaireViewModel::class.java) ->
                    FirstQuestionnaireViewModel(letsSwitchRepository)

                isAssignableFrom(ChatViewModel::class.java) ->
                    ChatViewModel(letsSwitchRepository)

                isAssignableFrom(MapViewModel::class.java)->
                    MapViewModel(letsSwitchRepository)

//
//                isAssignableFrom(CartViewModel::class.java) ->
//                    CartViewModel(letsSwitchReposityoryy)
//
//                isAssignableFrom(PaymentViewModel::class.java) ->
//                    PaymentViewModel(letsSwitchReposityory)
//
//                isAssignableFrom(LoginViewModel::class.java) ->
//                    LoginViewModel(letsSwitchReposityory)
//
//                isAssignableFrom(CheckoutSuccessViewModel::class.java) ->
//                    CheckoutSuccessViewModel(letsSwitchReposityory)
//
//                isAssignableFrom(HistoryViewModel::class.java) ->
//                    HistoryViewModel(letsSwitchReposityory)
//
//                isAssignableFrom(WebViewModel::class.java) ->
//                    WebViewModel(letsSwitchReposityory)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}