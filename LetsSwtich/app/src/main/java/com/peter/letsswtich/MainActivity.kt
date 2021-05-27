package com.peter.letsswtich

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView


import com.google.android.material.bottomnavigation.BottomNavigationView
import com.peter.letsswtich.data.Requirement
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.ActivityMainBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.login.UserManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var oldMatchList: List<User> = listOf()
    private var myEmail = UserManager.user.email

    val viewModel by viewModels<MainViewModel>{ getVmFactory() }


    private val onNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {

                        findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToFirstQuestionnaire())
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

                        findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToProfileFragment(UserManager.user))
                        return@OnNavigationItemSelectedListener true

                    }
                }
                false
            }

    private fun setupBottomNav() {
        binding.bottomNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

//        val menuView = binding.bottomNavView.getChildAt(0) as BottomNavigationMenuView
//        val itemView = menuView.getChildAt(2) as BottomNavigationItemView
//        val bindingBadge = BadgeBottomBinding.inflate(LayoutInflater.from(this), itemView, true)
//        bindingBadge.lifecycleOwner = this
//        bindingBadge.viewModel = viewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.matchList.observe(this, androidx.lifecycle.Observer {

            if (viewModel.matchList.value!!.size > oldMatchList.size && viewModel.oldMatchList.value != null) {

                val newPerson = it[0]

                Log.d("MainActivity","value of likelist = ${viewModel.likeList.value}")
                Log.d("MainActivity","value of newPerson = ${newPerson}")

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToMatchedDialog(newPerson))

                oldMatchList = it
            }
            if (viewModel.matchList.value!!.size < oldMatchList.size) {
                Log.d("HomeFragment", "Friends has been deleted")
                viewModel.getMyOldMatchList(myEmail)
            }

        })


        setupBottomNav()

    }
}