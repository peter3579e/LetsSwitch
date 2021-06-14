package com.peter.letsswtich.map.eventDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.databinding.FragmentEventDetailBinding
import com.peter.letsswtich.ext.getVmFactory

class EventDetailFragment:Fragment() {

    private lateinit var binding: FragmentEventDetailBinding
    private val TAG = "EventDetailFragment"

    private val viewModel by viewModels<EventDetailViewModel> {
        getVmFactory(
               EventDetailFragmentArgs.fromBundle(requireArguments()).eventDetail
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEventDetailBinding.inflate(inflater,container,false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.navigateBackToMap.observe(viewLifecycleOwner, Observer {
            if (it == true){
                findNavController().navigate(NavigationDirections.navigateToMapFragment())
                viewModel.mapNavigated()
            }
        })

        Log.d(TAG,"the value of received detail = ${viewModel.event}")

        return binding.root
    }
}