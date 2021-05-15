package com.peter.letsswtich.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.peter.letsswtich.databinding.DialogMatchedBinding

class MatchedDialog : AppCompatDialogFragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogMatchedBinding.inflate(inflater,container,false)

        return binding.root
    }
}