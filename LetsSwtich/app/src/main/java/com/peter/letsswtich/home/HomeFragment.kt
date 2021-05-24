package com.peter.letsswtich.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.FragmentHomeBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.network.LoadApiStatus
import com.yuyakaido.android.cardstackview.*
import com.peter.letsswtich.util.Logger
import java.util.*


class HomeFragment : Fragment(), CardStackListener {

    private val viewModel: HomeViewModel by viewModels<HomeViewModel> {
        getVmFactory(
                HomeFragmentArgs.fromBundle(requireArguments()).selectedAnswer
        )
    }

    private lateinit var binding: FragmentHomeBinding
    lateinit var adapter: HomeAdapter
    private var count = 0
    private lateinit var layoutManager: CardStackLayoutManager
    private val myEmail = UserManager.user.email
    private var maxCount: Int = -1
    var likedUser = requireNotNull(com.peter.letsswtich.data.User())
    private var oldMatchList: List<User> = listOf()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel


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

            if (viewModel.usersWithMatch == null) {
                matchValueVisibility(false)
            } else {
                matchValueVisibility(true)
            }
        })

        viewModel.userLikeList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it != null) {
                if (it.contains(myEmail)) {
                    Log.d("HomeFragment", "YesYes it is a match!")
                    findNavController().navigate(NavigationDirections.navigateToMatchedDialog(likedUser))
//                        Log.d("HomeFragment","The value of bigheadpic = ${likedUser.bigheadPic}")
//                        Log.d("HomeFragment","The value of bigheadpic = ${likedUser.name}")
                    Toast.makeText(LetsSwtichApplication.appContext, "It is a match!", Toast.LENGTH_SHORT).show()
                    viewModel.updateMatch(myEmail, likedUser)
                } else {
                    Log.d("HomeFragment", "No match!")
                }
            }
        })


        viewModel.oldMatchList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            oldMatchList = it
//            Log.d("HomeFragment", "value of old Matchlist = ${oldMatchList.size}")
        })

        viewModel.matchList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

//            Log.d("HomeViewFragment", "matchlist has detected!!")

            if (viewModel.matchList.value!!.size > oldMatchList.size && viewModel.oldMatchList.value != null) {
                val matchList: List<User> = viewModel.matchList.value!!
//                Log.d("HomeFragment", "new match List = ${matchList.size}")

                val newPerson = matchList - oldMatchList

//                Log.d("HomeFragment", "new match List = $likedUser")

                if (newPerson[0] != likedUser && likedUser != null) {
                    findNavController().navigate(NavigationDirections.navigateToMatchedDialog(newPerson[0]))
                }
//                        Log.d("HomeFragment","value of matchlist [0] = ${newPerson.size}")
//                        Log.d("HomeFragment","value of matchlist [0] = ${newPerson[0]}")
//                        Log.d("HomeFragment","value of matchlist [0] = ${oldMatchList.size}")
                oldMatchList = matchList
            }
            if (viewModel.matchList.value!!.size < oldMatchList.size) {
                Log.d("HomeFragment", "Friends has been deleted")
                viewModel.getMyOldMatchList(myEmail)
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

        maxCount = viewModel.allUser.value?.size!!

        viewModel.setRedBg(0f)
        viewModel.setBlueBg(0f)

        setupSwipeAction(direction)

        // Add count on every swipe & when count reaches max amount of the list, navigate.
        count++
//        if (count == maxAmount) {
//            findNavController().navigate(NavigationDirections.navigateToFollowListFragment())
//        }

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

//            viewModel.postUserToFollow(myEmail, requireNotNull(viewModel.usersWithMatch.value)[count])
            likedUser = requireNotNull(viewModel.allUser.value)[count]

            Log.d("HomeFragment", "value of like = $likedUser ")

            viewModel.updateMyLike(myEmail, likedUser)
            viewModel.getLikeList(myEmail, likedUser)
            viewModel.snapPosition.value = 0

//            Toast.makeText(LetsSwtichApplication.appContext, "Add to friendList", Toast.LENGTH_SHORT).show()
        }

        if (direction == Direction.Left) {
            likedUser = requireNotNull(viewModel.allUser.value)[count]
            Toast.makeText(LetsSwtichApplication.appContext, "Remove from likeList", Toast.LENGTH_SHORT).show()
            viewModel.removeFromLikeList(myEmail, likedUser)
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
//            binding.noValueButton.setOnClickListener {
//                findNavController().navigate(NavigationDirections.navigateToPairingFragment())
//            }
        }
    }

    private fun setupSearchAnimation(status: LoadApiStatus) {
        when (status) {
            LoadApiStatus.LOADING -> {
//                Log.d("HomeFragment", "LoadApi has Run")
                binding.layoutLoading.visibility = View.VISIBLE
                binding.animSearching.playAnimation()
            }
            LoadApiStatus.DONE -> {
                binding.layoutLoading.visibility = View.GONE
                binding.animSearching.cancelAnimation()
//                Log.d("HomeFragment", "LoadApi has Done")
            }
            else -> Toast.makeText(context, "Something Terrible Happened", Toast.LENGTH_SHORT).show()
        }
    }

}