package com.peter.letsswtich.map.eventDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.R
import com.peter.letsswtich.bindRecyclerViewByCount
import com.peter.letsswtich.databinding.FragmentEventDetailBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.home.ImageCircleAdapter
import com.peter.letsswtich.login.UserManager

class EventDetailFragment : Fragment() {

    private lateinit var binding: FragmentEventDetailBinding
    private val TAG = "EventDetailFragment"
    private val viewModel by viewModels<EventDetailViewModel> {
        getVmFactory(
            EventDetailFragmentArgs.fromBundle(requireArguments()).eventDetail
        )
    }

    override fun onStart() {
        super.onStart()

        for (pic in viewModel.event.eventPhotos) {
            if (pic != "") {
                viewModel.photoList.add(pic)
            }
        }
        Log.d(TAG, "the value of photos = ${viewModel.photoList}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val adapter = EventDetailImageAdapter(viewModel)
        binding.recyclerDetailGallery.adapter = adapter
        val circleAdapter = ImageCircleAdapter()
        binding.recyclerDetailCircles.adapter = circleAdapter
        val joinAdapter = JoinListAdapter(viewModel)
        binding.joinListRecycler.adapter = joinAdapter

        val linearSnapHelper = LinearSnapHelper().apply {
            val snapHelper: SnapHelper = PagerSnapHelper()
            binding.recyclerDetailGallery.onFlingListener = null
            snapHelper.attachToRecyclerView(binding.recyclerDetailGallery)
        }

        binding.recyclerDetailGallery.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            viewModel.onCampaignScrollChange(
                binding.recyclerDetailGallery.layoutManager,
                linearSnapHelper
            )
        }

        viewModel.snapPosition.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            (binding.recyclerDetailCircles.adapter as ImageCircleAdapter).selectedPosition.value =
                (it % (viewModel.photoList.size))

        })

        viewModel.join.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                viewModel.postJoin(UserManager.user.email, viewModel.event)
                Toast.makeText(
                    LetsSwtichApplication.appContext,
                    getString(R.string.joined),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.joinSent()
            }
        })

        var count = 0

        var joinListPic: MutableList<String>? = mutableListOf()

        viewModel.jointList.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "the value of joinList = $it")
            for (pic in it) {
                joinListPic!!.add(pic.personImages[0])
                Log.d(TAG, "the value of joinList Pic = $joinListPic")
                count++
                Log.d(TAG, "the value of count = ${count}")
            }

            if (count == it.size) {
                Log.d(TAG, "the value of joinList Before= $joinListPic")
                joinAdapter.submitList(joinListPic)
                joinAdapter.notifyDataSetChanged()
                joinListPic = mutableListOf()
                Log.d(TAG, "the value of joinList After = $joinListPic")
                count = 0
            }
        })

        viewModel.navigateBackToMap.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().navigate(NavigationDirections.navigateToMapFragment())
                viewModel.mapNavigated()
            }
        })

        Log.d(TAG, "the value of received detail = ${viewModel.event}")
        return binding.root
    }
}