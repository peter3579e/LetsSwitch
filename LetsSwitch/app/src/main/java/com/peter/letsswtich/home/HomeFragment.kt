package com.peter.letsswtich.home

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.MainViewModel
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.databinding.FragmentHomeBinding
import com.peter.letsswtich.ext.excludeUser
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.network.LoadApiStatus
import com.yuyakaido.android.cardstackview.*
import com.peter.letsswtich.util.Logger


class HomeFragment : Fragment(), CardStackListener {

    val viewModel by viewModels<HomeViewModel> { getVmFactory() }

    private lateinit var binding: FragmentHomeBinding
    lateinit var adapter: HomeAdapter
    private var count = 0
    private lateinit var layoutManager: CardStackLayoutManager
    private val myEmail = UserManager.user.email
    private var maxCount: Int = -1
    var likedUser = requireNotNull(com.peter.letsswtich.data.User())

    override fun onStart() {
        super.onStart()

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        Log.d("UserManager", "value of UserManager = ${UserManager.user}")

        viewModel.requirement.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { requirement ->

                Log.d("HomeFragment", "value of requirment = ${viewModel.requirement.value}")

                requirement?.let {
                    viewModel.getAllUser()
                    mainViewModel.requirement.value = requirement
                    mainViewModel.userDetail.observe(
                        viewLifecycleOwner,
                        androidx.lifecycle.Observer {

                            UserManager.user.preferLanguage = listOf(requirement.language)
                            it.preferLanguage = listOf(requirement.language)
                        })
                    Log.d("HomeFragment", "the value of Use with requirement = ${UserManager.user}")
                }

            })

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        binding.image = UserManager.user.personImages[0]


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
            if (this is DefaultItemAnimator) {
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
            if (count == 1 || count == 2) {
                viewModel.count = true
            }
            binding.stackView.rewind()
        }


        viewModel.allUser.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewModel.filteredUserList(it)
            Log.d("HomeFragment", "value of match User = ${viewModel.usersWithMatch.value}")

        })

        viewModel.usersWithMatch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            val sortedList = it.excludeUser()

            if (sortedList.isEmpty()) {
                matchValueVisibility(false)
            } else {
                matchValueVisibility(true)
            }
            adapter.submitList(sortedList)
        })


        viewModel.userLikeList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it != null) {
                if (it.contains(myEmail)) {
                    Log.d("HomeFragment", "YesYes it is a match!")
                    findNavController().navigate(
                        NavigationDirections.navigateToMatchedDialog(
                            likedUser
                        )
                    )
//                        Log.d("HomeFragment","The value of bigheadpic = ${likedUser.bigheadPic}")
//                        Log.d("HomeFragment","The value of bigheadpic = ${likedUser.name}")
                    Toast.makeText(
                        LetsSwtichApplication.appContext,
                        "It is a match!",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.updateMatch(myEmail, likedUser)
                    Log.d("HomeFragment", "value of likedUser = $likedUser")
                } else {
                    Log.d("HomeFragment", "No match!")
                }
            }
        })

        var count = 0
        var oldMatch = 0

        viewModel.matchList.observe(viewLifecycleOwner, androidx.lifecycle.Observer { newMatch ->

            Log.d("HomeFragmentBefroe", "value of count = $count")

            for (i in newMatch) {
                Log.d("HomeFragment", "the name of match user = ${i.name}")
            }

            if (viewModel.matchList.value!!.isNotEmpty()) {

                Log.d("the match list", "the value of name ${newMatch[0].name} ")
                Log.d("HomeFragmentBefore", "newMatchSize = ${newMatch.size}")
                Log.d("HomeFragmentBefore", "oldMatchSize = ${oldMatch}")
                val newPerson = newMatch[0]
                if (count > 0 && newMatch.size > oldMatch && newPerson != likedUser) {
                    Log.d("HomeFragment", "match has run!!!!")
                    findNavController().navigate(
                        NavigationDirections.navigateToMatchedDialog(
                            newPerson
                        )
                    )
                }
                oldMatch = newMatch.size
                count++

                Log.d("HomeFragmentAfter", "newMatchSize = ${newMatch.size}")
                Log.d("HomeFragmentAfter", "oldMatchSize = ${oldMatch}")
                Log.d("HomeFragmentAfter", "value of count = $count")
            } else if (viewModel.matchList.value!!.isEmpty()) {
                oldMatch = 0
                Logger.d("else if is working")
                Log.d("HomeFragmentAfter", "oldMatchSize = ${oldMatch}")
            }
        })

        viewModel.status.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            setupSearchAnimation(it)
        })

        viewModel.snapPosition.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

        })



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

        maxCount = viewModel.usersWithMatch.value?.size!!

        viewModel.setRedBg(0f)
        viewModel.setBlueBg(0f)

        setupSwipeAction(direction)

        // Add count on every swipe & when count reaches max amount of the list, navigate.
        count++
        Logger.i(count.toString())
        Logger.i(maxCount.toString())

    }

    override fun onCardCanceled() {

        viewModel.setRedBg(0f)
        viewModel.setBlueBg(0f)

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardRewound() {
        viewModel.snapPosition.value = 0
        count--
    }

    private fun setupSwipeAction(direction: Direction?) {
        if (direction == Direction.Right) {
            likedUser = requireNotNull(viewModel.usersWithMatch.value)[count]
            Log.d("HomeFragment", "value of like = $likedUser ")

            viewModel.updateMyLike(myEmail, likedUser)
            viewModel.getLikeList(myEmail, likedUser)
            viewModel.snapPosition.value = 0

        }

        if (direction == Direction.Left) {
            likedUser = requireNotNull(viewModel.usersWithMatch.value)[count]
            Toast.makeText(
                LetsSwtichApplication.appContext,
                "Remove from likeList",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.removeFromLikeList(myEmail, likedUser)
            viewModel.removeUserFromChatList(myEmail, likedUser.email)
            viewModel.snapPosition.value = 0
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

    private fun matchValueVisibility(withValue: Boolean) {
        if (withValue) {
            binding.noValueText.visibility = View.GONE
            binding.noValueButton.visibility = View.GONE
        } else {
            binding.noValueText.visibility = View.VISIBLE
            binding.noValueButton.visibility = View.VISIBLE
        }
    }

    private fun setupSearchAnimation(status: LoadApiStatus) {
        when (status) {
            LoadApiStatus.LOADING -> {
                binding.layoutLoading.visibility = View.VISIBLE
                binding.userPic.visibility = View.VISIBLE
                binding.animSearching.playAnimation()
            }
            LoadApiStatus.DONE -> {
                Handler().postDelayed({
                    binding.layoutLoading.visibility = View.GONE
                    binding.userPic.visibility = View.GONE
                    binding.animSearching.cancelAnimation()
                }, 2000)
            }
            else -> Toast.makeText(context, "Something Terrible Happened", Toast.LENGTH_SHORT)
                .show()
        }
    }

}