package com.peter.letsswtich.map

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.peter.letsswtich.*
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.FragmentMapBinding
import com.peter.letsswtich.ext.FORMAT_YYYY_MM_DDHHMMSS
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.ext.toDateFormat
import com.peter.letsswtich.login.LoginActivity
import com.peter.letsswtich.login.UserManager
import kotlinx.coroutines.*
import java.io.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS


class MapFragment : Fragment(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private val MAPVIEW_BUNDLE_KEY: String? = "MapViewBundleKey"
    private lateinit var map: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var markerOld: Marker? = null
    private val LOCATION_PERMISSION_REQUEST = 1
    private var TAG = "MapFragment"

    private val viewModel: MapViewModel by viewModels<MapViewModel> { getVmFactory() }

    override fun onMarkerClick(marker: Marker?): Boolean {
        markerOld?.let {
            it.alpha = 0.5F
        }

        marker?.let {
            markerOld = it
            it.alpha = 1F
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        mainViewModel.friendsProfileNavigated()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val adpter = FriendsImageAdapter(viewModel)
        binding.friendsRecycleView.adapter = adpter
        binding.friendsRecycleView.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            context,
            R.anim.recycler_animation
        )
        val eventAdapter = EventListAdapter(viewModel)
        binding.recyclerEventList.adapter = eventAdapter

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        mainViewModel.matchList.observe(viewLifecycleOwner, Observer {
            viewModel.matchList.value = it
            Log.d("the matchlist", "the value of matchlist = ${viewModel.matchList.value}")

            val userEmail = mutableListOf<String>()
            for (users in it) {
                userEmail.add(users.email)
                viewModel.getUserDetail(users.email)
            }

            val list = mutableListOf<User>()
            val images = mutableListOf<String>()

            var count = 1

            viewModel.userInfo.observe(viewLifecycleOwner, Observer { userInfo ->
                list.add(userInfo)
                images.add(userInfo.personImages[0])

                if (count == userEmail.size) {
                    viewModel.listofMatchUserInfo.value = list
                    mainViewModel.newestFriendDetail.value = list
                    Log.d(
                        "MapFragment",
                        "the livedata list = ${viewModel.listofMatchUserInfo.value!!.size}"
                    )
                    Log.d("the value of images", "the value of images = ${images.size}")
                    viewModel.imagesLive.value = images
                    adpter.submitList(list)
                }

                count++

            })
        })

        viewModel.showsMore.observe(viewLifecycleOwner, Observer {
            binding.detail.visibility = View.VISIBLE
        })

        viewModel.navigateToProfile.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().navigate(
                    NavigationDirections.navigateToProfileFragment(
                        viewModel.clickedUserDetail.value!!,
                        true
                    )
                )
                viewModel.profilenavigated()
            }
        })

        viewModel.cardViewHeight.observe(viewLifecycleOwner, Observer { height ->
            Log.d("MapFragment", "cardView Height = ${binding.cardView.height}")
            binding.friendButton.animate().translationY(-height.toFloat())
        })



        viewModel.cardView.observe(viewLifecycleOwner, Observer {
            if (it % 2 == 0) {
                binding.cardView.visibility = View.VISIBLE
                binding.cardView.x = 1500F
                viewModel.cardViewHeight.value = 440
                binding.detail.visibility = View.GONE
                binding.eventButton.visibility = View.GONE
                binding.cardView.animate().translationX(0F)
                Log.d("MapFragment", "the value of count cardView in show = $it")

            } else if (it % 2 != 0) {
                binding.eventButton.visibility = View.VISIBLE
                binding.cardView.animate().translationX(1500F)
                binding.friendButton.animate().translationY(0F)
                Log.d("MapFragment", "the value of count cardView in gone = $it")
            }
        })

        viewModel.event.observe(viewLifecycleOwner, Observer {
            if (it % 2 == 0) {
                binding.eventScroll.visibility = View.VISIBLE
                binding.eventButton.visibility = View.GONE
                binding.friendButton.visibility = View.GONE
                binding.eventScroll.y = 1500F
                binding.eventScroll.animate().translationY(0F)
                Log.d("MapFragment", "the value of count event in show = $it")

            } else if (it % 2 != 0) {
                binding.eventButton.visibility = View.VISIBLE
                binding.friendButton.visibility = View.VISIBLE
                binding.eventScroll.animate().translationY(1500F)
                Log.d("MapFragment", "the value of count event in gone = $it")
            }
        })

        viewModel.createEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().navigate(NavigationDirections.navigateToEventFragment())
                viewModel.createEventNavigated()
            }
        })



        viewModel.navigateToChatRoom.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().navigate(
                    NavigationDirections.navigateToChatroomFragment(
                        viewModel.clickedUserDetail.value!!.email,
                        viewModel.clickedUserDetail.value!!.name,
                        true
                    )
                )
                viewModel.chatRoomNavigated()
            }
        })

        viewModel.allEvent.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "the vale of all event = ${it.size}")
            eventAdapter.submitEvent(it)
            viewModel.eventsInMapReady.value = it

        })


        map = binding.radarMap
        initGoogleMap(savedInstanceState)

        return binding.root
    }


    private fun initGoogleMap(savedInstanceState: Bundle?) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        map.onCreate(mapViewBundle)
        map.getMapAsync(this)
    }


    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        viewModel.eventsInMapReady.observe(this, Observer {
            for (events in it) {
                val location = LatLng(events.Location.latitude, events.Location.lngti)
                googleMap.addMarker(
                    MarkerOptions().position(location)
                        .flat(true)
                )
            }
        })

        if (ActivityCompat.checkSelfPermission(
                LetsSwtichApplication.appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                LetsSwtichApplication.appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                activity as MainActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient((LetsSwtichApplication.appContext))

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { myLocation ->
            val newLocation = LatLng(myLocation.latitude, myLocation.longitude)

            Log.d("MapFragment", "newlocation value = $newLocation")

            viewModel.mylocation.value = newLocation

            Log.d("location", "my email = ${UserManager.user.email}")

            viewModel.mylocation.observe(viewLifecycleOwner, Observer { location ->

                viewModel.postlocaion(location.longitude, location.latitude, UserManager.user.email)

                Log.d("location", "the value of lat = ${location}")
            })

            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    newLocation,
                    15.toFloat()
                )
            )

            val queryRadius = 1

            val circleOptions = CircleOptions()
            circleOptions.center(newLocation)
                .radius(queryRadius.toDouble() * 500)
                .fillColor(Color.argb(70, 150, 50, 50))
                .strokeWidth(3F)
                .strokeColor(Color.RED)
            googleMap.addCircle(circleOptions)

        }

        viewModel.friendslocation.observe(viewLifecycleOwner, Observer {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    it,
                    15.toFloat()
                )
            )
        })

        viewModel.clickedEventLocation.observe(this, Observer { Event ->
            val location = LatLng(Event.Location.latitude, Event.Location.lngti)

            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    15.toFloat()
                )
            )
        })

        viewModel.navigateToEventDetail.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(NavigationDirections.navigateToEventDetailFragment(it))
                viewModel.navigateToEventDetail.value = null
            }
        })


        viewModel.listofMatchUserInfo.observe(this, Observer { usersList ->
            usersList.let {

                for (userInfo in usersList) {

                    val queryResult = LatLng(
                        userInfo.latitude, userInfo.lngti
                    )

                    Log.d("MapFragment", "value of queryResult = $queryResult")

                    val widthIcon = Resources.getSystem().displayMetrics.widthPixels / 10
                    val bitmapDraw =
                        LetsSwtichApplication.instance.getDrawable(R.drawable.drink_map_icon_1)
                    val b = bitmapDraw?.toBitmap()
                    val smallMarker =
                        Bitmap.createScaledBitmap(b!!, widthIcon, widthIcon, false)
                    val iconDraw = BitmapDescriptorFactory.fromBitmap(smallMarker)

                    val image = userInfo.personImages[0]

                    Log.d("Peter", "value of = $image")

                    val url = URL(image)

                    val result: Deferred<Bitmap?> = GlobalScope.async {
                        url.toBitmap()
                    }

                    GlobalScope.launch(Dispatchers.Main) {
                        // show bitmap on image view when available
                        val figureMarker = result.await()?.let { it1 ->
                            Bitmap.createScaledBitmap(
                                it1, widthIcon, widthIcon, false
                            )

                        }

                        val addMarker = googleMap.addMarker(
                            MarkerOptions().position(queryResult)
                                .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        createCustomMarker(
                                            requireContext(),
                                            figureMarker!!,
                                            "Narender"
                                        )
                                    )
                                )
                                .snippet(userInfo.name)
                                .title(userInfo.description)
                        )
                        addMarker.tag = userInfo
                    }
                }
            }
        })
    }

    // extension function to get bitmap from url
    fun URL.toBitmap(): Bitmap? {
        return try {
            BitmapFactory.decodeStream(openStream())
        } catch (e: IOException) {
            null
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map.onDestroy()
    }
}

