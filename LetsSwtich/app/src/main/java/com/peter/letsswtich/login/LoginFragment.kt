package com.peter.letsswtich.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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
import com.peter.letsswtich.*
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.FragmentLoginBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login_process.LoginProcessActivity
import com.peter.letsswtich.util.CurrentFragmentType


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    val viewModel by viewModels<LoginViewModel> { getVmFactory() }
    private var auth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var GOOGLE_LOGIN_CODE = 9001

    override fun onStart() {
        super.onStart()
//        moveMainPage(auth?.currentUser)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        binding.googleSignInButton.setOnClickListener {

            if (binding.privacy.isChecked) {
                googleLogin()
            } else {
                Toast.makeText(
                    LetsSwtichApplication.appContext,
                    getString(R.string.privacy_requiremnt),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Configure Google Sign In inside onCreate mentod
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.token_id))
            .requestEmail()
            .build()
        // getting the value of gso inside the GoogleSigninClient
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.term.setOnClickListener {
            findNavController().navigate(LoginNavigationDirections.navigateToLoginPrivacyDialog())
            binding.privacy.isChecked = true
        }
        binding.policy.setOnClickListener {
            findNavController().navigate(LoginNavigationDirections.navigateToLoginPrivacyDialog())
            binding.privacy.isChecked = true
        }

        binding.privacy.setOnClickListener {
            binding.privacy.isChecked = true
        }

        viewModel.firebaseUser.observe(viewLifecycleOwner, Observer {
            it?.let {
                moveToQuestion(it)
                Log.d("user", "the observe has run = $it")
            }
        })
        return binding.root
    }

    private fun googleLogin() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_LOGIN_CODE) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                Log.d("LoginActivity", "the value of result = ${result.isSuccess}")
                if (result.isSuccess) {
                    val account = result.signInAccount
                    //Second step
                    viewModel.loginAuth(account)
                }
            }
        }
    }


    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            val currentUser = User(
                personImages = listOf(user.photoUrl.toString()),
                email = user.email.toString(),
                name = user.displayName.toString(),
                googleId = user.uid
            )
            UserManager.user = currentUser
            Log.d("LogActivity", "the value of User =${UserManager.user}")
            startActivity(Intent(context, LoginProcessActivity::class.java))
            requireActivity().overridePendingTransition(0, android.R.anim.fade_out)
            requireActivity().finish()
        }
    }

    private fun moveToQuestion(user: FirebaseUser?) {
        if (user != null) {
            val currentUser = User(
                personImages = listOf(user.photoUrl.toString()),
                email = user.email.toString(),
                name = user.displayName.toString(),
                googleId = user.uid
            )

            UserManager.user = currentUser
            UserManager.uid = user.email.toString()
            Log.d("LogActivity", "the value of User =${UserManager.user}")

            findNavController().navigate(LoginNavigationDirections.navigateToLoginProcess())
        }
    }

}