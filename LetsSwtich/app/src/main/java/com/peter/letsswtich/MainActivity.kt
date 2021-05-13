package com.peter.letsswtich

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView


import com.google.android.material.bottomnavigation.BottomNavigationView
import com.peter.letsswtich.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

        setupBottomNav()

    }
}