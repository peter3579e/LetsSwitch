package com.peter.letsswtich.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.peter.letsswtich.LoginNavigationDirections
import com.peter.letsswtich.MainActivity
import com.peter.letsswtich.NavigationDirections
import com.peter.letsswtich.R
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.FragmentLoginBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login_process.LoginProcessActivity
import com.peter.letsswtich.util.CurrentFragmentType


class LoginFragment :Fragment(){

    private lateinit var binding: FragmentLoginBinding

    val viewModel by viewModels<LoginViewModel> { getVmFactory() }
    private var auth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var GOOGLE_LOGIN_CODE = 9001

    override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
        Log.d("LoginActivity","Run4")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentLoginBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()



        binding.googleSignInButton.setOnClickListener {
            googleLogin()
            Log.d("LoginActivity","Run1")
        }

        // Configure Google Sign In inside onCreate mentod
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.token_id))
                .requestEmail()
                .build()
        Log.d("LoginActivity","Run2")
// getting the value of gso inside the GoogleSigninClient
        googleSignInClient = GoogleSignIn.getClient(requireContext(),gso)
        Log.d("LoginActivity","Run3")
// initialize the firebaseAuth variable

        viewModel.firebaseUser.observe(viewLifecycleOwner, Observer {
            it?.let {
//                findNavController().navigate(LoginNavigationDirections.navigateToFirstQuestionnaire())
                moveToQuestion(it)

                Log.d("user","the observe has run = $it")
            }
        })


        return binding.root
    }

    private fun googleLogin() {
        val signInIntent = googleSignInClient?.signInIntent
        Log.d("LoginActivity","Run5")
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("LoginActivity","google has return")

        if (requestCode == GOOGLE_LOGIN_CODE) {
            Log.d("LoginActivity","google has return 1")
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.d("LoginActivity","google has return 2")
            if (result != null) {
                Log.d("LoginActivity","google has return 3")
                Log.d("LoginActivity","the value of result = ${result.isSuccess}")
                if (result.isSuccess) {
                    Log.d("LoginActivity","google has return 4")
                    val account = result.signInAccount
                    //Second step
                    viewModel.loginAuth(account)
                }
            }
        }

//        if(requestCode==GOOGLE_LOGIN_CODE){
//            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleResult(task)
//        }
    }


    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            Log.d("LoginActivity","google has return 5")
            val currentUser = User(personImages = listOf(user.photoUrl.toString()),
                    email = user.email.toString(),
                    name = user.displayName.toString(),
                    googleId = user.uid
            )

            UserManager.user = currentUser

            Log.d("LogActivity","PostUser has run")
            Log.d("LogActivity","the value of User =${UserManager.user}")
//            viewModel.postUser(currentUser)

//            startActivity(Intent(this, SplashActivity::class.java))
//            startActivity(Intent(context, MainActivity::class.java))
            startActivity(Intent(context,LoginProcessActivity::class.java))

            requireActivity().overridePendingTransition(0, android.R.anim.fade_out)
            requireActivity().finish()
        }
    }

    private fun moveToQuestion(user: FirebaseUser?) {
        if (user != null) {
            Log.d("LoginActivity","google has return 5")
            val currentUser = User(personImages = listOf(user.photoUrl.toString()),
                    email = user.email.toString(),
                    name = user.displayName.toString(),
                    googleId = user.uid
            )

            UserManager.user = currentUser
            UserManager.uid = user.email.toString()

            Log.d("LogActivity","PostUser has run")
            Log.d("LogActivity","the value of User =${UserManager.user}")
//            viewModel.postUser(currentUser)

            findNavController().navigate(LoginNavigationDirections.navigateToLoginProcess())

            Log.d("LogActivity","thing has changed jhon show")



//            startActivity(Intent(this, SplashActivity::class.java))
//            startActivity(Intent(context, MainActivity::class.java))
//            requireActivity().overridePendingTransition(0, android.R.anim.fade_out)
        }
    }

}