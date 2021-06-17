package com.peter.letsswtich.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.peter.letsswtich.R
import com.peter.letsswtich.databinding.DialogMatchedBinding
import com.peter.letsswtich.databinding.DialogPicBinding
import com.peter.letsswtich.dialog.MatchedDialogArgs
import com.peter.letsswtich.dialog.MatchedDialogViewModel
import com.peter.letsswtich.ext.getVmFactory

class PicDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<PicViewModel> {
        getVmFactory(
            PicDialogArgs.fromBundle(requireArguments()).userImage
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
        val binding = DialogPicBinding.inflate(inflater, container, false)
        binding.dialog = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.image = viewModel.image


        return binding.root
    }
}