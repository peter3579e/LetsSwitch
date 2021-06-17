package com.peter.letsswtich.login_process

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.peter.letsswtich.MainActivity
import com.peter.letsswtich.R
import com.peter.letsswtich.databinding.ActivityLoginProcessBinding

class LoginProcessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginProcessBinding
    private val duration = 2500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_process)

        Handler().postDelayed({

            startActivity(Intent(this, MainActivity::class.java))

            overridePendingTransition(0, android.R.anim.fade_out)

            finish()

        }, duration)
    }
}