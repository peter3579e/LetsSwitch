package com.peter.letsswtich.login_process

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.peter.letsswtich.LoginNavigationDirections
import com.peter.letsswtich.MainActivity
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.databinding.FragmentLoginProcessBinding

class LoginProcessFragment : Fragment() {

    private lateinit var binding: FragmentLoginProcessBinding
    private val duration = 2500L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginProcessBinding.inflate(inflater, container, false)
        Handler().postDelayed({
            findNavController().navigate(LoginNavigationDirections.navigateToFirstQuestionnaire())
            requireActivity().overridePendingTransition(0, android.R.anim.fade_out)

        }, duration)

        return binding.root
    }
}