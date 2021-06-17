package com.peter.letsswtich.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.peter.letsswtich.MainActivity
import com.peter.letsswtich.R
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.ActivityLoginBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.util.CurrentFragmentType

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding

    val viewModel by viewModels<LoginActivityViewModel> { getVmFactory() }

//    val viewModel by viewModels<LoginViewModel> { getVmFactory() }
//    private var auth: FirebaseAuth? = null
//    private var googleSignInClient: GoogleSignInClient? = null
//    private var GOOGLE_LOGIN_CODE = 9001

    private fun setupNavController(){
        findNavController(R.id.loginNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id){
                R.id.firstQuestionnaireFragment -> CurrentFragmentType.FIRSTQUESTION
                R.id.secondQutionnaireFragment -> CurrentFragmentType.SECONDQUESTION
                R.id.loginProcessFragment -> CurrentFragmentType.LOGINPROCESS
                else -> viewModel.currentFragmentType.value
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        setupNavController()



    }

}