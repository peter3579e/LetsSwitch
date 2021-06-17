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
import androidx.lifecycle.ViewModelProvider
import com.peter.letsswtich.MainViewModel
import com.peter.letsswtich.R
import com.peter.letsswtich.data.ChatRoom
import com.peter.letsswtich.data.UserInfo
import com.peter.letsswtich.databinding.FragmentChatBinding
import com.peter.letsswtich.ext.getVmFactory
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




        viewModel.allLiveChatRooms.observe(viewLifecycleOwner, Observer { oldList ->
            oldList.let {
                Log.d("ChatFragment", "value of allLiveChatRoom = $oldList")
                val filteredChatRoom = mutableListOf<ChatRoom>()

                if (oldList.isEmpty()) {
                    Logger.d("no value has run!!")
                    binding.noValue.visibility = View.VISIBLE
                    binding.noValueImage.visibility = View.VISIBLE
                } else {

                    oldList.forEach {chatRoom ->

                        // Remove my info to make the new info list contains only the other user's info
                        chatRoom.attendeesInfo = excludeMyInfo(chatRoom.attendeesInfo)

                        filteredChatRoom.add(chatRoom)
                    }

                    Log.d("ChatFragment","value of filteredChatRoom = ${filteredChatRoom.size}")

                    viewModel.createFilteredChatRooms(filteredChatRoom)
                }

            }
        })


        viewModel.filteredChatRooms.observe(viewLifecycleOwner, Observer { list ->


//            Log.d("ChatFragment", "value of filteredChatRoom=${list.sortedByDescending { it.latestMessageTime }}")

            val newList = list.sortedByDescending { it.latestMessageTime }

            for(i in newList){
                Log.d("ChatFragment","value of list = ${i.latestMessageTime}]")
            }

            Log.d("ChatFragment","value of list = $list")



                viewModel.roomByMessageTime.value = list
                binding.recyclerChatList.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.recycler_animation)

                    newMatchesAdapter.submitList(list)
                    adapter.submitList(newList)

            }
        )

        return binding.root
    }

    private fun excludeMyInfo (attendeesInfo: List<UserInfo>) : List<UserInfo>{
        return attendeesInfo.filter {
            it.userEmail != UserManager.user.email
        }
    }
}