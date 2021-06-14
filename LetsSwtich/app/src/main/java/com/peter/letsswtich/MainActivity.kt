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
    private var myEmail = UserManager.user.email

    val viewModel by viewModels<MainViewModel>{ getVmFactory() }



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

//        val menuView = binding.bottomNavView.getChildAt(0) as BottomNavigationMenuView
//        val itemView = menuView.getChildAt(2) as BottomNavigationItemView
//        val bindingBadge = BadgeBottomBinding.inflate(LayoutInflater.from(this), itemView, true)
//        bindingBadge.lifecycleOwner = this
//        bindingBadge.viewModel = viewModel
    }



    private fun setupNavController(){
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id){
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

//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0
//            val window: Window = window
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) // 確認取消半透明設置。
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // 全螢幕顯示，status bar 不隱藏，activity 上方 layout 會被 status bar 覆蓋。
//                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE) // 配合其他 flag 使用，防止 system bar 改變後 layout 的變動。
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) // 跟系統表示要渲染 system bar 背景。
//            window.setStatusBarColor(Color.TRANSPARENT)
//        }

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




//        viewModel.matchList.observe(this, androidx.lifecycle.Observer {newMatch ->
//
//            Log.d("MainActivity","value of new size = ${newMatch.size}")
//
//            viewModel.getMyOldMatchList(myEmail)
//
//        viewModel.oldMatchList.observe(this, Observer { oldMatch ->
//
//            for (i in newMatch){
//                Log.d("Mainactivity","the value of = ${i.name}")
//            }
//
//            Log.d("MainActivity","value of old size = ${oldMatch.size}")
//
//
//            if (newMatch.size > oldMatch.size && viewModel.oldMatchList.value != null && viewModel.currentFragmentType.value != CurrentFragmentType.HOME) {
//
//                val newPerson = newMatch[0]
////                Log.d("MainActivity","value of likelist = ${viewModel.likeList.value}")
////                Log.d("MainActivity","value of newPerson = ${newPerson}")
//                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToMatchedDialog(newPerson))
//                viewModel.oldMatchList.value = newMatch
//            }
//
//            if (viewModel.matchList.value!!.size < viewModel.oldMatchList.value!!.size) {
////                Log.d("HomeFragment", "Friends has been deleted")
//                viewModel.getMyOldMatchList(myEmail)
//            }
//        })
//
//        })

        viewModel.userInfo.observe(this, Observer {
            UserManager.user = it
            viewModel.userdetail.value = it
            Log.d("Mainactivity", "the vaue of User Manager = ${UserManager.user}")
            Log.d("Mainactivity", "the vaue of userdetail = ${viewModel.userdetail.value}")

        })


        var count = 0
        var oldMatch =0


        viewModel.matchList.observe(this, androidx.lifecycle.Observer { newMatch ->

//            viewModel.getChatRoom()

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