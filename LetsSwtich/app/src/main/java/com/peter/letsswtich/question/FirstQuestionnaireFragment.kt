package com.peter.letsswtich.question

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.chat.ChatViewModel
import com.peter.letsswtich.data.Requirement
import com.peter.letsswtich.databinding.FragmentFirstQuestionnaireBinding
import com.peter.letsswtich.ext.getVmFactory

class FirstQuestionnaireFragment:Fragment() {

    private lateinit var binding: FragmentFirstQuestionnaireBinding

    private val viewModel : FirstQuestionnaireViewModel by viewModels<FirstQuestionnaireViewModel> { getVmFactory() }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstQuestionnaireBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.button.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToHomeFragment(Requirement("Female","English", listOf(0,100),"Taipei")))
        }

        binding.button2.setOnClickListener{
            viewModel.postUser()
        }


        return binding.root
    }
}