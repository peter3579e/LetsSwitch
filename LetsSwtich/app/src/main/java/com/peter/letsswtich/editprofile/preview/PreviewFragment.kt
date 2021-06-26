package com.peter.letsswtich.editprofile.preview


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.chip.Chip
import com.peter.letsswtich.MainViewModel
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.FragmentPreviewBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.home.ImageCircleAdapter

class PreviewFragment(user: User): Fragment() {

    private lateinit var binding :FragmentPreviewBinding

    private var userdetail = user

    private val viewModel : PreviewViewModel by viewModels<PreviewViewModel> { getVmFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentPreviewBinding.inflate(inflater,container,false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.userDetail = userdetail
        val chipGroup = binding.chipGroup
        val language = viewModel.userDetail.fluentLanguage

        val imageAdapter = PreviewImageAdapter(viewModel)

        binding.imageCardUser.adapter = imageAdapter

        val circleAdapter = ImageCircleAdapter()

        binding.recyclerImageCircles.adapter = circleAdapter

        mainViewModel.userDetail.observe(viewLifecycleOwner, Observer {
            Log.d("PreviewFragment","the value of userdetail from main viewModel = ${it}")
            viewModel.userDetail.personImages = it.personImages
            imageAdapter.submitImages(viewModel.userDetail.personImages)
            imageAdapter.notifyDataSetChanged()
            circleAdapter.submitCount(userdetail.personImages.size)
            binding.name = it.name
            binding.city = it.city
            binding.district = it.district
            val newlanguage = mainViewModel.userDetail.value!!.fluentLanguage
            if (mainViewModel.userDetail.value!!.fluentLanguage != viewModel.userDetail.fluentLanguage){
                for (language in newlanguage){
                    val chip = Chip(chipGroup.context)
                    chip.text = language
                    chipGroup.addView(chip)
                }
            }
        })


        val linearSnapHelper = LinearSnapHelper().apply {
            val snapHelper: SnapHelper = PagerSnapHelper()
            binding.imageCardUser.onFlingListener = null
            snapHelper.attachToRecyclerView(binding.imageCardUser)
        }

        binding.imageCardUser.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            viewModel.onCampaignScrollChange(
                    binding.imageCardUser.layoutManager,
                    linearSnapHelper
            )
        }

        viewModel.snapPosition.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

                (binding.recyclerImageCircles.adapter as ImageCircleAdapter).selectedPosition.value =
                        (it % (viewModel.userDetail.personImages.size))

        })

        val layoutManager = binding.imageCardUser.layoutManager
        binding.cardImagePlus.setOnClickListener {
                viewModel.snapPosition.value?.let {
                    if(viewModel.snapPosition.value!!< viewModel.userDetail.personImages.size-1){
                        layoutManager?.smoothScrollToPosition(
                                binding.imageCardUser, RecyclerView.State(),
                                it.plus(1)

                        )
                    }
                    Log.d("timer", "position ${it}")
                }
        }

        binding.cardImageMinus.setOnClickListener {
                viewModel.snapPosition.value?.let {
                    if (viewModel.snapPosition.value!! > 0) {
                        layoutManager?.smoothScrollToPosition(
                                binding.imageCardUser, RecyclerView.State(),
                                it.minus(1)
                        )
                    }
                    Log.d("timer", "position ${it}")
                }
        }


        for (language in language){
            val chip = Chip(chipGroup.context)
            chip.text = language
            chipGroup.addView(chip)
        }


        return binding.root
    }

}