package com.peter.letsswtich.chatroom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.MainActivity
import com.peter.letsswtich.R
import com.peter.letsswtich.databinding.FragmentChatRoomBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login.UserManager


class ChatRoomFragment : Fragment() {

    private val viewModel by viewModels<ChatRoomViewModel> {
        getVmFactory(
            ChatRoomFragmentArgs.fromBundle(requireArguments()).userEmail!!, ChatRoomFragmentArgs.fromBundle(requireArguments()).userName!!
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

        binding = FragmentChatRoomBinding.inflate(inflater,container,false)
        val adapter = ChatRoomAdapter()
        binding.viewModel = viewModel
        binding.recyclerMessage.adapter = adapter
        binding.lifecycleOwner = this
//        viewModel.getMessageItem()

//        val friendUserEmail = viewModel.currentChattingUser
        val myUserEmail = UserManager.user.email

        Log.d("ChatRoomFragment"," valuse of useremail = $myUserEmail")

        // Setup custom toolbar
        if (activity is MainActivity) {
            (activity as MainActivity).setSupportActionBar(binding.toolbar)
        }

        binding.send.setOnClickListener {
            if (isEmpty()) {
                Toast.makeText(LetsSwtichApplication.appContext, getString(R.string.reminder_chatroom_message), Toast.LENGTH_SHORT).show()
            }
            else {
                sendMessage()
            }
        }

        // Observers
        viewModel.allLiveMessage.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })



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
            findNavController().navigateUp()
            return true
        }
        return false
    }

    private fun sendMessage() {
//        viewModel.postMessage(viewModel.getUserEmails(myEmail, friendEmail), viewModel.getMessage())
        binding.editMessage.text.clear()
    }
}