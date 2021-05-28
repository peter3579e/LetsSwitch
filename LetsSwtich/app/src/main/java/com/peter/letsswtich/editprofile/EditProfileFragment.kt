package com.peter.letsswtich.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.peter.letsswtich.databinding.FragmentEditProfileBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.home.HomeFragmentArgs
import com.peter.letsswtich.profile.ProfileFragmentArgs
import com.peter.letsswtich.profile.ProfileViewModel

class EditProfileFragment : Fragment(){

    private lateinit var binding: FragmentEditProfileBinding

//    private val viewModel by viewModels<EditProfileViewModel> {
//        getVmFactory(
//            EditProfileFragmentArgs.fromBundle(requireArguments()).userDetail
//        )
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater,container,false)
//        binding.viewModel = viewModel

        return binding.root
    }
}