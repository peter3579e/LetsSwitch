package com.peter.letsswtich.dialog

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.R
import com.peter.letsswtich.chatroom.ChatRoomFragmentArgs
import com.peter.letsswtich.chatroom.ChatRoomViewModel
import com.peter.letsswtich.databinding.DialogMatchedBinding
import com.peter.letsswtich.ext.getVmFactory

class MatchedDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<MatchedDialogViewModel> {
        getVmFactory(
                MatchedDialogArgs.fromBundle(requireArguments()).matchedUserInfo
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MessageDialog)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogMatchedBinding.inflate(inflater,container,false)
        binding.dialog = this
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel


        return binding.root
    }


//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        view.setOnClickListener{
//            Log.d("click","click")
//            this.dismiss()
//        }
//    }
}