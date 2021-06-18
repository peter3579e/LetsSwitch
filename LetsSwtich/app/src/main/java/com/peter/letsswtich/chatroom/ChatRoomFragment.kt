package com.peter.letsswtich.chatroom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.peter.letsswtich.*
import com.peter.letsswtich.databinding.FragmentChatRoomBinding
import com.peter.letsswtich.ext.excludeFriend
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.util.Logger


class ChatRoomFragment : Fragment() {

    private val viewModel by viewModels<ChatRoomViewModel> {
        getVmFactory(
            ChatRoomFragmentArgs.fromBundle(requireArguments()).userEmail,
            ChatRoomFragmentArgs.fromBundle(requireArguments()).userName,
            ChatRoomFragmentArgs.fromBundle(requireArguments()).fromMap
        )
    }
    private lateinit var binding: FragmentChatRoomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        val adapter = ChatRoomAdapter(viewModel)
        binding.viewModel = viewModel
        binding.recyclerMessage.adapter = adapter
        binding.lifecycleOwner = this

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        mainViewModel.friendsProfileNavigated()

        val friendUserEmail = viewModel.currentChattingUser
        val myEmail = UserManager.user.email

        // Setup custom toolbar
        if (activity is MainActivity) {
            (activity as MainActivity).setSupportActionBar(binding.toolbar)
        }


        //check the send box if empty
        binding.send.setOnClickListener {
            if (isEmpty()) {
                Toast.makeText(
                    LetsSwtichApplication.appContext,
                    getString(R.string.reminder_chatroom_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                sendMessage(myEmail, friendUserEmail)
            }
        }

        //get all Live Message from firebase
        viewModel.allLiveMessage.observe(viewLifecycleOwner, Observer { message ->
            viewModel.updateIsRead(viewModel.currentChattingUser, message.documentId)
            //exclude friends message
            viewModel.filterMessage = message.message.excludeFriend(friendUserEmail)
            Log.d("ChatroomFragment", "the value of message ${message.message}")
            viewModel.count = 0
            adapter.submitList(message.message)
            adapter.notifyDataSetChanged()

            Log.d("ChatRoomFragmen", "the value of filteredMessage = ${viewModel.filterMessage}")
        })

        //check if send message icon is blue
        viewModel.enterMessage.observe(viewLifecycleOwner, Observer {
            Logger.d(it)
            binding.sendwithblue.visibility = View.VISIBLE

            if (it == "") {
                binding.sendwithblue.visibility = View.GONE
            }
        })

        binding.editMessage.doOnTextChanged { text, start, before, count ->
            viewModel.enterMessage.value = text.toString()
            Log.d("Peter", "${viewModel.enterMessage.value}")
        }
        return binding.root
    }

    private fun isEmpty(): Boolean {
        return when (viewModel.enterMessage.value) {
            null -> true
            else -> false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (viewModel.ifMap) {
                findNavController().navigate(NavigationDirections.navigateToMapFragment())
            } else {
                findNavController().navigate(NavigationDirections.navigateToChatFragment())
            }
            return true
        }
        return false
    }

    private fun sendMessage(myEmail: String, friendEmail: String) {
        viewModel.postMessage(viewModel.getUserEmails(myEmail, friendEmail), viewModel.getMessage())
        binding.editMessage.text.clear()
    }
}