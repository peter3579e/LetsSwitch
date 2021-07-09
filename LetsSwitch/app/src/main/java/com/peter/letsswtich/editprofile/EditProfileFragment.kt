package com.peter.letsswtich.editprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.peter.letsswtich.*
import com.peter.letsswtich.databinding.FragmentEditProfileBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login.UserManager

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private val viewModel by viewModels<EditProfileViewModel> {
        getVmFactory(
            EditProfileFragmentArgs.fromBundle(requireArguments()).userDetail
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.viewPager.let {
            binding.tabLayout.setupWithViewPager(it)
            it.adapter = ViewpagerAdapeter(childFragmentManager, viewModel)
        }


        if (activity is MainActivity) {
            (activity as MainActivity).setSupportActionBar(binding.toolbar)
        }

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        fun isFinished(): Boolean {

            return when {
                mainViewModel.userDetail.value!!.fluentLanguage.isNotEmpty() &&
                        mainViewModel.userDetail.value!!.gender != "" -> {
                    true
                }

                else -> {
                    Toast.makeText(
                        LetsSwtichApplication.appContext,
                        getString(R.string.remindertofillInfor),
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                }
            }

        }

        viewModel.navigateToProfile.observe(viewLifecycleOwner, Observer {
            if (it == true && isFinished()) {
                Log.d(
                    "EditProfileFragment",
                    "the value of User = ${mainViewModel.userDetail.value}"
                )
                UserManager.user = mainViewModel.userDetail.value!!
                viewModel.user = mainViewModel.userDetail.value!!
                Log.d("EditProfileFragment", "the value of UserManager = ${UserManager.user}")
                findNavController().navigate(
                    NavigationDirections.navigateToProfileFragment(
                        viewModel.user,
                        false
                    )
                )
                viewModel.updateUser(mainViewModel.userDetail.value!!)
                viewModel.profileNavigated()
            }
        })
        return binding.root
    }


}