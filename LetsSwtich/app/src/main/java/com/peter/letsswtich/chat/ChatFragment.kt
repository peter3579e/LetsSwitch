package com.peter.letsswtich.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.peter.letsswtich.R
import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.data.UserInfo
import com.peter.letsswtich.databinding.FragmentChatBinding
import com.peter.letsswtich.databinding.FragmentHomeBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.home.HomeViewModel
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.util.Logger

class ChatFragment:Fragment() {

    private lateinit var binding: FragmentChatBinding

    private val viewModel : ChatViewModel by viewModels<ChatViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater,container,false)

        binding.viewModel = viewModel
        val adapter = ChatListAdapter(viewModel)
        val newMatchesAdapter = NewMatchedAdapter(viewModel)
        binding.recyclerChatList.adapter = adapter
        binding.recyclerNewMatches.adapter = newMatchesAdapter
        binding.lifecycleOwner = this


        viewModel.matchList.observe(viewLifecycleOwner, Observer {
            Log.d("ChatFragment","the value of matchlist = ${viewModel.matchList.value!!.size}")
            viewModel.getChatRoom()
        })


        viewModel.allLiveChatRooms.observe(viewLifecycleOwner, Observer {
            it.let {
                Log.d("ChatFragment", "value of allLiveChatRoom = $it")
                val filteredChatRoom = mutableListOf<ChatRoom>()

                it.forEach {chatRoom ->

                    // Remove my info to make the new info list contains only the other user's info
                    chatRoom.attendeesInfo = excludeMyInfo(chatRoom.attendeesInfo)

                    filteredChatRoom.add(chatRoom)
                }

                viewModel.createFilteredChatRooms(filteredChatRoom)

            }
        })

        viewModel.filteredChatRooms.observe(viewLifecycleOwner, Observer {
            Log.d("ChatFragment", "value of filteredChatRoom=$it")
            it?.let {

//                binding.recyclerChatList.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.recycler_animation)

                if (it.isEmpty()) {
                    binding.noValue.visibility = View.VISIBLE
                    binding.noValueImage.visibility = View.VISIBLE
                } else {
                    adapter.submitList(it)
                    newMatchesAdapter.submitList(it)
                }
            }
        })

        return binding.root
    }

    private fun excludeMyInfo (attendeesInfo: List<UserInfo>) : List<UserInfo>{
        return attendeesInfo.filter {
            it.userEmail != UserManager.user.email
        }
    }
}