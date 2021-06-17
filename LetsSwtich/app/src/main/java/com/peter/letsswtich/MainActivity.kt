package com.peter.letsswtich

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.peter.letsswtich.databinding.ActivityMainBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.util.CurrentFragmentType
import com.peter.letsswtich.util.Logger


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel by viewModels<MainViewModel> { getVmFactory() }


    private val onNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {

                        findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToHomeFragment())
                        return@OnNavigationItemSelectedListener true

                    }
                    R.id.navigation_chat -> {

                        findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToChatFragment())
                        return@OnNavigationItemSelectedListener true

                    }
                    R.id.navigation_map -> {

                        findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToMapFragment())
                        return@OnNavigationItemSelectedListener true

                    }
                    R.id.navigation_profile -> {

                        Log.d("MainActivity", "UserManager value = ${UserManager.user}")

                        findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToProfileFragment(UserManager.user, false))

                        return@OnNavigationItemSelectedListener true

                    }
                }
                false
            }

    private fun setupBottomNav() {
        binding.bottomNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

    }


    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.homeFragment -> {
                    val home = binding.bottomNavView.menu.findItem(R.id.navigation_home)
                    home.isChecked = true
                    CurrentFragmentType.HOME
                }
                R.id.chatFragment -> CurrentFragmentType.CHAT
                R.id.mapFragment -> CurrentFragmentType.MAP
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.chatRoomFragment -> CurrentFragmentType.CHATROOM
                R.id.editProfileFragment -> CurrentFragmentType.EDITPROFILE
                R.id.firstQuestionnaireFragment -> CurrentFragmentType.FIRSTQUESTION
                R.id.secondQutionnaireFragment -> CurrentFragmentType.SECONDQUESTION
                R.id.settingFragment -> CurrentFragmentType.SETTING
                R.id.editEventFragment -> CurrentFragmentType.EDITEVENT
                R.id.eventDetailFragment -> CurrentFragmentType.EVENTDETAIL
                else -> viewModel.currentFragmentType.value
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        binding.bottomNavView.visibility = View.GONE

        setupBottomNav()
        setupNavController()

        Log.d("initail", "the initial value of UserManage =${UserManager.user}")


        viewModel.currentFragmentType.observe(this, Observer {
            Logger.i("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            Logger.i("[${viewModel.currentFragmentType.value}]")
            Logger.i("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
        })


        viewModel.userInfo.observe(this, Observer {
            UserManager.user = it
            viewModel.userdetail.value = it
            Log.d("Mainactivity", "the vaue of User Manager = ${UserManager.user}")
            Log.d("Mainactivity", "the vaue of userdetail = ${viewModel.userdetail.value}")

        })


        var count = 0
        var oldMatch = 0


        viewModel.matchList.observe(this, androidx.lifecycle.Observer { newMatch ->


            Log.d("Before", "value of count = $count")
            if (viewModel.matchList.value!!.isNotEmpty() && viewModel.currentFragmentType.value != CurrentFragmentType.HOME) {

                Log.d("the match list", "the value of name ${newMatch[0].name} ")
                Log.d("Before", "newMatchSize = ${newMatch.size}")
                Log.d("Before", "oldMatchSize = ${oldMatch}")
                if (count > 0 && newMatch.size > oldMatch) {
                    Log.d("match", "match has run!!!!")
                    val newPerson = newMatch[0]
                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToMatchedDialog(newPerson))
                }
                oldMatch = newMatch.size
                count++

                Log.d("After", "newMatchSize = ${newMatch.size}")
                Log.d("After", "oldMatchSize = ${oldMatch}")
                Log.d("After", "value of count = $count")
            } else if (viewModel.matchList.value!!.isEmpty()) {
                oldMatch = 0
                Logger.d("else if is working")
                Log.d("After", "oldMatchSize = ${oldMatch}")
            }
        })


    }

}