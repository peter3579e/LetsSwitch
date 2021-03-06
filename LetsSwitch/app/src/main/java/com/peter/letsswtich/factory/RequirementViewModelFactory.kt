package com.peter.letsswtich.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.data.Requirement
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.home.HomeViewModel
import com.peter.letsswtich.setting.SettingViewModel

class RequirementViewModelFactory constructor(
    private val repository: LetsSwitchRepository,
    private val userRequirement: Requirement
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(SettingViewModel::class.java) ->
                    SettingViewModel(repository, userRequirement)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}