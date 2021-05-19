package com.peter.letsswtich.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.MainActivity
import com.peter.letsswtich.MapsActivity
import com.peter.letsswtich.R
import com.peter.letsswtich.data.StoreLocation
import com.peter.letsswtich.databinding.FragmentMapBinding
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.home.HomeViewModel
import com.peter.letsswtich.util.Logger
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.cos

class MapFragment : Fragment(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private val MAPVIEW_BUNDLE_KEY: String? = "MapViewBundleKey"
    private lateinit var map: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var markerOld: Marker? = null
    private val LOCATION_PERMISSION_REQUEST = 1

    private val viewModel: MapViewModel by viewModels<MapViewModel> { getVmFactory() }

    override fun onMarkerClick(marker: Marker?): Boolean {
        markerOld?.let {
            it.alpha = 0.5F
        }

        marker?.let {
            markerOld = it
            it.alpha = 1F
            val storeLocation = (it.tag as StoreLocation)
            val store = storeLocation.store
            viewModel.selectStore(storeLocation)
            viewModel.storeCardOpen()
        }
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

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
            Log.d("Run", "456")
        } else {
            ActivityCompat.requestPermissions(
                activity as MainActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }


        viewModel.storeLocation.observe(this, Observer {
            it.let { storeLocationList ->


                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient((LetsSwtichApplication.appContext))

                fusedLocationProviderClient.lastLocation.addOnSuccessListener { myLocation ->
                    val newLocation = LatLng(myLocation.latitude, myLocation.longitude)
                    Log.d("MapFragment","Has run here!")
                    Log.d("MapFragment", "newlocation value = $newLocation")
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


                    for (storeLocation in storeLocationList) {


                        val queryResult = LatLng(
                            storeLocation.latitude
                            , storeLocation.longitude
                        )

                        Log.d("MapFragment","value of queryResult = $queryResult")

                        val widthIcon = Resources.getSystem().displayMetrics.widthPixels / 10
                        val bitmapDraw = LetsSwtichApplication.instance.getDrawable(R.drawable.drink_map_icon_1)
                        val b = bitmapDraw?.toBitmap()
                        val smallMarker =
                            Bitmap.createScaledBitmap(b!!, widthIcon, widthIcon, false)
                        val iconDraw = BitmapDescriptorFactory.fromBitmap(smallMarker)

//                        if (queryResult.latitude in lowerLat..greaterLat
//                            && queryResult.longitude in lowerLon..greaterLon
//                        ) {

                        val image = storeLocation.store.uri

                        Log.d("Peter","value of = $image")

                        val url  = URL(image)


                        val result: Deferred<Bitmap?> = GlobalScope.async {
                            url.toBitmap()
                        }


                        GlobalScope.launch(Dispatchers.Main) {
                            // show bitmap on image view when available
                            val figureMarker = result.await()?.let { it1 ->
                                Bitmap.createScaledBitmap(
                                    it1, widthIcon, widthIcon, false)
                            }

                            val addMarker = googleMap.addMarker(
                                MarkerOptions().position(queryResult)
                                    .flat(true)
//                                    .alpha(0.5F)
//                                .icon(iconDraw)
                                    .icon(BitmapDescriptorFactory.fromBitmap(figureMarker))
                                    .snippet(storeLocation.branchName)
                                    .title(storeLocation.store.storeName)
                            )
                            addMarker.tag = storeLocation

                        }

//                        }

//                        googleMap.animateCamera(
//                            CameraUpdateFactory.newLatLngZoom(
//                                queryResult,
//                                15.toFloat()
//                            )
//                        )
                    }
                }

            }
        })
    }

//    private fun googleMapNav(geoPoint: LatLng) {
//        fusedLocationProviderClient =
//            LocationServices.getFusedLocationProviderClient((activity as MainActivity))
//        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
//            val storeLnt = geoPoint.latitude
//            val storeLon = geoPoint.longitude
//            val intent = Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse(
//                    "http://maps.google.com/maps?"
//                            + "saddr=" + it.latitude + "," + it.longitude
//                            + "&daddr=" + storeLnt + "," + storeLon
//                            + "&avoid=highway"
//                            + "&language=zh-CN"
//                )
//            )
//            intent.setClassName(
//                "com.google.android.apps.maps",
//                "com.google.android.maps.MapsActivity"
//            )
//            startActivity(intent)
//        }
//    }

    // extension function to get bitmap from url
    fun URL.toBitmap(): Bitmap?{
        return try {
            BitmapFactory.decodeStream(openStream())
        }catch (e: IOException){
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

