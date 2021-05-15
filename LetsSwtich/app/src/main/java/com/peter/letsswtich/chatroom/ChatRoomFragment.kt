package com.peter.letsswtich.chatroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.letsswtich.databinding.FragmentChatRoomBinding

class ChatRoomFragment : Fragment() {
    private lateinit var binding: FragmentChatRoomBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChatRoomBinding.inflate(inflater,container,false)

        return binding.root
    }
}