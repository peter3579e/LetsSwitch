package com.peter.letsswtich.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.peter.letsswtich.R
import com.peter.letsswtich.databinding.DialogPrivacyPolicyBinding

class LoginPrivacy : AppCompatDialogFragment() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MessageDialog)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogPrivacyPolicyBinding.inflate(inflater, container, false)
        webView = binding.webview
        val webSetting = webView.settings
        webSetting.javaScriptEnabled = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        binding.lifecycleOwner = viewLifecycleOwner
        webView.loadUrl("https://www.privacypolicies.com/live/9da8eba7-9bde-4944-b899-ce158608365d")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.setOnClickListener {
            this.dismiss()
        }
    }
}