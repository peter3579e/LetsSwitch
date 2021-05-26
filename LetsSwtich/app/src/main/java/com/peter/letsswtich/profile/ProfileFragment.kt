package com.peter.letsswtich.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.peter.letsswtich.databinding.FragmentProfileBinding
import com.peter.letsswtich.dialog.MatchedDialogArgs
import com.peter.letsswtich.dialog.MatchedDialogViewModel
import com.peter.letsswtich.ext.getVmFactory

class ProfileFragment: Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel by viewModels<ProfileViewModel> {
        getVmFactory(
                ProfileFragmentArgs.fromBundle(requireArguments()).userDetail
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentProfileBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val nativeChip = binding.chipGroup
        val chip = Chip(nativeChip.context)
        chip.text = viewModel.userDetail.fluentLanguage[0]
        nativeChip.addView(chip)
        val fluentChip = binding.chipAlso
        val chip2 = Chip(fluentChip.context)
        chip2.text = viewModel.userDetail.fluentLanguage[1]
        fluentChip.addView(chip2)

        val adapter = PhotosAdapter()

        binding.photosRecycleView.adapter = adapter


        for(language in viewModel.userDetail.preferLanguage){
            val learningChip = binding.chipLearning
            val chip3 = Chip(learningChip.context)
            chip3.text = language
            learningChip.addView(chip3)
        }

        val imageList : MutableList<String> = mutableListOf()

        for(images in viewModel.userDetail.personImages){
            if (images == viewModel.userDetail.personImages[0]){
                Log.d("ProfileFragment","Nothing happened")
            }else{
                imageList.add(images)
            }
        }

        adapter.submitList(imageList)


        return binding.root
    }
}