package com.peter.letsswtich.home

import android.os.Bundle
import android.util.Log
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.databinding.FragmentHomeBinding
import com.peter.letsswtich.ext.getVmFactory
import com.yuyakaido.android.cardstackview.*
import com.peter.letsswtich.util.Logger


class HomeFragment : Fragment(), CardStackListener {

    private val viewModel : HomeViewModel by viewModels<HomeViewModel> { getVmFactory() }

    private lateinit var binding: FragmentHomeBinding
    lateinit var adapter: HomeAdapter
    private var count = 0
    private lateinit var layoutManager: CardStackLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)


        binding.lifecycleOwner=viewLifecycleOwner

        binding.viewModel=viewModel

//        viewModel.cardProduct.observe(viewLifecycleOwner, Observer {
//            Log.d("Peter","value of = ${viewModel.cardProduct.value}")
//        })

        viewModel.getUserItem()

        // Setup card stack recyclerview
        val stackView = binding.stackView
        adapter = HomeAdapter(viewModel)
        layoutManager = CardStackLayoutManager(requireContext(), this).apply {
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
            setMaxDegree(20.0f)
            setStackFrom(StackFrom.Top)
        }

        stackView.layoutManager = layoutManager
        stackView.adapter = adapter
        stackView.itemAnimator.apply {
            if (this is DefaultItemAnimator){
                supportsChangeAnimations = false
            }
        }



        binding.buttonYes.setOnClickListener {

            setupSwipeAnimation(Direction.Left)
            binding.stackView.swipe()

        }

        binding.buttonNo.setOnClickListener {

            setupSwipeAnimation(Direction.Right)
            binding.stackView.swipe()

        }

        binding.buttonRewind.setOnClickListener {
            binding.stackView.rewind()
        }

        viewModel.navigateToProfilePage.observe(viewLifecycleOwner, Observer {
            if (viewModel.navigateToProfilePage.value == true){
                findNavController().navigate(NavigationDirections.navigateToProfileFragment())
                viewModel.onProfileNavigated()
        }
        })

//        viewModel.createSortedList()
//
//        viewModel.usersWithMatch.observe(viewLifecycleOwner, Observer {
//
////            val sortedList = it.excludeUser()
////
////            if (sortedList.isEmpty()) {
////                matchValueVisibility(false)
////            } else {
////                matchValueVisibility(true)
////            }
//            adapter.submitList(viewModel.cardProduct.value)
//        })



        return binding.root
    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

        when (direction) {
            Direction.Left -> viewModel.setRedBg(ratio)
            Direction.Right -> viewModel.setBlueBg(ratio)
            Direction.Right -> viewModel.setBlueBg(ratio)
            else -> {
                viewModel.setRedBg(0f)
                viewModel.setBlueBg(0f)
            }
        }
    }

    override fun onCardSwiped(direction: Direction?) {

        val maxAmount = viewModel.cardProduct.value?.size

        viewModel.setRedBg(0f)
        viewModel.setBlueBg(0f)

        setupSwipeAction(direction)

        // Add count on every swipe & when count reaches max amount of the list, navigate.
        count++
//        if (count == maxAmount) {
//            findNavController().navigate(NavigationDirections.navigateToFollowListFragment())
//        }

        Logger.i(count.toString())
        Logger.i(maxAmount.toString())

    }

    override fun onCardCanceled() {

        viewModel.setRedBg(0f)
        viewModel.setBlueBg(0f)

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardRewound() {
        count--
    }

    private fun setupSwipeAction(direction: Direction?) {
        if (direction == Direction.Right) {

//            viewModel.postUserToFollow(myEmail, requireNotNull(viewModel.usersWithMatch.value)[count])

            Toast.makeText(LetsSwtichApplication.appContext, "Add to friendList", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSwipeAnimation(direction: Direction) {
        val setting = SwipeAnimationSetting.Builder()
            .setDirection(direction)
            .setDuration(Duration.Normal.duration)
            .setInterpolator(AccelerateInterpolator())
            .build()
        layoutManager.setSwipeAnimationSetting(setting)
    }

//    private fun setupSearchAnimation (status: LoadApiStatus) {
//        when (status) {
//            LoadApiStatus.LOADING -> {
//                binding.layoutLoading.visibility = View.VISIBLE
//                binding.animSearching.playAnimation()
//            }
//            LoadApiStatus.DONE -> {
//                binding.layoutLoading.visibility = View.GONE
//                binding.animSearching.cancelAnimation()
//            }
//            else -> Toast.makeText(context, "Something Terrible Happened", Toast.LENGTH_SHORT).show()
//        }
//    }

}