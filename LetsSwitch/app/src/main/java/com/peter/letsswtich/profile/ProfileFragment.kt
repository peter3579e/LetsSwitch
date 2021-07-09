package com.peter.letsswtich.profile

import android.Manifest
import android.app.Activity
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
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.peter.letsswtich.*
import com.peter.letsswtich.databinding.FragmentProfileBinding
import com.peter.letsswtich.ext.FORMAT_YYYY_MM_DDHHMMSS
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.ext.toDateFormat
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.map.createCustomMarker
import kotlinx.coroutines.*
import java.io.*
import java.net.URL


class  ProfileFragment : Fragment(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel by viewModels<ProfileViewModel> {
        getVmFactory(
                ProfileFragmentArgs.fromBundle(requireArguments()).userDetail,
                ProfileFragmentArgs.fromBundle(requireArguments()).fromMap
        )
    }

    private val MAPVIEW_BUNDLE_KEY: String? = "MapViewBundleKey"
    private lateinit var map: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var markerOld: Marker? = null
    private val LOCATION_PERMISSION_REQUEST = 1


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.backgroundImage = viewModel.userDetail.backGroundPic

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)


        if (viewModel.userDetail.email != UserManager.user.email) {
            binding.buttonBack.visibility = View.VISIBLE
            binding.editProfileButton.visibility = View.GONE
            binding.changeBackground.visibility = View.GONE
            binding.setting.visibility = View.GONE
            mainViewModel.navigateToFriendProfile()
            if (viewModel.userDetail.fluentLanguage.isNotEmpty()) {
                val nativeChip = binding.chipGroup
                val chip = Chip(nativeChip.context)
                chip.text = viewModel.userDetail.fluentLanguage[0]
                nativeChip.addView(chip)
                val fluentChip = binding.chipAlso
                val chip2 = Chip(fluentChip.context)
                chip2.text = viewModel.userDetail.fluentLanguage[1]
                fluentChip.addView(chip2)
                viewModel.locatMe.value = false
            }

            if (viewModel.userDetail.preferLanguage.isNotEmpty()) {
                for (language in viewModel.userDetail.preferLanguage) {
                    val learningChip = binding.chipLearning
                    val chip3 = Chip(learningChip.context)
                    chip3.text = language
                    learningChip.addView(chip3)
                }
            }

            Log.d(
                    "ProfileFragment",
                    "valeu of navigation = ${mainViewModel.navigateToFriendsProfile.value}"
            )
            binding.buttonBack.setOnClickListener {
                if (viewModel.ifMap == true) {
                    findNavController().navigate(NavigationDirections.navigateToMapFragment())
                } else {
                    findNavController().navigate(
                            NavigationDirections.navigateToChatroomFragment(
                                    viewModel.userDetail.email,
                                    viewModel.userDetail.name,
                                    false
                            )
                    )
                }
            }

        } else {
            if (UserManager.user.fluentLanguage.isNotEmpty()) {
                val nativeChip = binding.chipGroup
                val chip = Chip(nativeChip.context)
                chip.text = viewModel.userDetail.fluentLanguage[0]
                nativeChip.addView(chip)
                val fluentChip = binding.chipAlso
                val chip2 = Chip(fluentChip.context)
                chip2.text = viewModel.userDetail.fluentLanguage[1]
                fluentChip.addView(chip2)
                viewModel.locatMe.value = true
            }

            if (UserManager.user.preferLanguage.isNotEmpty()) {
                for (language in viewModel.userDetail.preferLanguage) {
                    val learningChip = binding.chipLearning
                    val chip3 = Chip(learningChip.context)
                    chip3.text = language
                    learningChip.addView(chip3)
                }
            }
        }

        viewModel.picDialog.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().navigate(NavigationDirections.navigateToPicDialog(viewModel.userDetail.personImages[0]))
            } else if (it == false) {
                findNavController().navigate(NavigationDirections.navigateToPicDialog(viewModel.userDetail.backGroundPic))
            }
        })



        viewModel.navigateToEditProfile.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().navigate(
                        NavigationDirections.navigateToEditProfilePage(
                                viewModel.userDetail
                        )
                )
                Log.d("ProfileFragment", "the value of = ${viewModel.userDetail}")
                viewModel.editProfileNavigated()
            }
        })

        viewModel.setting.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().navigate(
                        NavigationDirections.navigateToSettingFragment(
                                mainViewModel.requirement.value!!
                        )
                )
                viewModel.settingNavigated()
            }
        })


        val adapter = PhotosAdapter(viewModel)

        binding.photosRecycleView.adapter = adapter

        viewModel.clickedPic.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(NavigationDirections.navigateToPicDialog(it))
        })


        val imageList: MutableList<String> = mutableListOf()

        for (images in viewModel.userDetail.personImages) {
            if (images == viewModel.userDetail.personImages[0]) {
                Log.d("ProfileFragment", "Nothing happened")
            } else {
                imageList.add(images)
            }
        }

        adapter.submitList(imageList)

        viewModel.camera.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                activateCamera()
                viewModel.closeCamera()
            }
        })

        viewModel.photoUri.observe(viewLifecycleOwner, Observer {
            Log.d("profileFragment", "Uri has received")
            viewModel.userDetail.backGroundPic = it.toString()
            binding.backgroundImage = viewModel.userDetail.backGroundPic
            Log.d("profileFragment", "Uri has received $it")
            mainViewModel.userDetail.value!!.backGroundPic = it.toString()
            UserManager.user.backGroundPic = it.toString()
            Log.d(
                    "profileFragment",
                    "User with background ${mainViewModel.userDetail.value!!.backGroundPic}"
            )
            Log.d(
                    "profileFragment",
                    "UserManager with background ${UserManager.user.backGroundPic}"
            )
            viewModel.updateUser(mainViewModel.userDetail.value!!)

        })

        map = binding.mapView
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


    private fun activateCamera() {
        getPermissions()
        if (isUploadPermissionsGranted) {

            selectImage()
        } else if (!isUploadPermissionsGranted) {

            Toast.makeText(
                    LetsSwtichApplication.applicationContext(),
                    LetsSwtichApplication.applicationContext()
                            .getString(R.string.edit_upload_permission_hint),
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showGallery() {

        val intent = Intent()
        intent.type = LetsSwtichApplication.applicationContext()
                .getString(R.string.edit_show_gallery_intent_type)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
                Intent.createChooser(
                        intent, LetsSwtichApplication.applicationContext()
                        .getString(R.string.edit_show_gallery_select_picture)
                ),
                IMAGE_FROM_GALLERY
        )
    }

    // Create an image file name
    private fun createImageFile(): File {

        //This is the directory in which the file will be created. This is the default location of Camera photos
        val storageDir = File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ),
                LetsSwtichApplication.applicationContext().getString(R.string.edit_start_camera_camera)
        )

        return File.createTempFile(
                viewModel.date.value.toDateFormat(FORMAT_YYYY_MM_DDHHMMSS),  /* prefix */
                LetsSwtichApplication.applicationContext()
                        .getString(R.string.edit_start_camera_jpg), /* suffix */
                storageDir      /* directory */
        )
    }

    //handling the image chooser activity result
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode !== Activity.RESULT_CANCELED) {


            if (resultCode == Activity.RESULT_OK) {

                when (requestCode) {
                    IMAGE_FROM_GALLERY -> {

                        data?.let {

                            it.data?.let { data ->


                                filePath = data

                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(
                                            (activity as MainActivity).contentResolver, filePath
                                    )

                                    val matrix = Matrix()

                                    matrix.postRotate(
                                            getImageRotation(
                                                    LetsSwtichApplication.applicationContext(),
                                                    data
                                            ).toFloat()
                                    )

                                    val outBitmap = Bitmap.createBitmap(
                                            bitmap!!, 0, 0,
                                            bitmap!!.width, bitmap!!.height, matrix, false
                                    )

                                    val byte = ByteArrayOutputStream()

                                    outBitmap.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            15,
                                            byte
                                    )

                                    val byteArray = byte.toByteArray()

                                    uploadFile(byteArray)

                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                    IMAGE_FROM_CAMERA -> {

                        fileFromCamera?.let {

                            Log.d("Max", "the value of file path =  $fileFromCamera")

                            bitmap = data?.extras?.get("data") as Bitmap

                            Log.d("Max", "$bitmap")

                            val matrix = Matrix()

                            val outBitmap = Bitmap.createBitmap(
                                    bitmap!!, 0, 0,
                                    bitmap!!.width, bitmap!!.height, matrix, false
                            )

                            val byte = ByteArrayOutputStream()

                            outBitmap.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    15,
                                    byte
                            )

                            val byteArray = byte.toByteArray()

                            uploadCamera(byteArray)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getImageRotation(context: Context, uri: Uri): Int {
        var stream: InputStream? = null
        return try {
            stream = context.contentResolver.openInputStream(uri)
            val exifInterface = ExifInterface(stream!!)
            val exifOrientation =
                    exifInterface.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                    )
            when (exifOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            0
        } finally {
            stream?.close()
        }
    }

    fun selectImage() {

        val items = arrayOf<CharSequence>(
                LetsSwtichApplication.applicationContext().resources.getText(com.peter.letsswtich.R.string.edit_add_photo),
                LetsSwtichApplication.applicationContext().resources.getText(com.peter.letsswtich.R.string.edit_choose_from_gallery),
                LetsSwtichApplication.applicationContext().resources.getText(com.peter.letsswtich.R.string.edit_cancel)
        )

        val context = this.context

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(LetsSwtichApplication.applicationContext().resources.getText(com.peter.letsswtich.R.string.edit_add_photo_title))
        builder.setItems(items) { dialog, item ->
            if (items[item] == LetsSwtichApplication.applicationContext().resources.getText(com.peter.letsswtich.R.string.edit_cancel)) {

                dialog.dismiss()
            } else {

                chooseCameraOrGallery = items[item].toString()
                callCameraOrGallery()
            }
        }

        builder.show()
    }

    private fun callCameraOrGallery() {

        chooseCameraOrGallery?.let {

            when (it) {

                LetsSwtichApplication.applicationContext().resources.getString(R.string.edit_add_photo) -> {

                    startCamera()
                }
                LetsSwtichApplication.applicationContext().resources.getString(R.string.edit_choose_from_gallery) -> {

                    showGallery()
                }
            }
        }

    }

    private fun uploadCamera(bitmap: ByteArray) {

        viewModel.uploadPhoto()

        UserManager.uid?.let { uid ->

            // Firebase storage
            auth = FirebaseAuth.getInstance()

            Log.d("Peter", "the value of date = ${viewModel.date.value}")

            val imageReference = FirebaseStorage.getInstance().reference.child(
                    LetsSwtichApplication.applicationContext().getString(
                            R.string.firebase_storage_reference, uid, viewModel.date.value.toDateFormat(
                            FORMAT_YYYY_MM_DDHHMMSS
                    )
                    )
            ).child(fileFromCamera.toString())

            imageReference.putBytes(bitmap)
                    .addOnCompleteListener {


                        imageReference.downloadUrl.addOnCompleteListener { task ->

                            task.result?.let { taskResult ->

                                Log.d("Peter", "the result of pic = $taskResult")

                                viewModel.setPhoto(taskResult)
                            }
                        }
                    }

        }
    }

    private fun uploadFile(byteArray: ByteArray) {

        filePath?.let { filePath ->

            viewModel.uploadPhoto()

            UserManager.uid?.let { uid ->

                // Firebase storage
                auth = FirebaseAuth.getInstance()

                Log.d("Peter", "the value of date = ${viewModel.date.value}")

                val imageReference = FirebaseStorage.getInstance().reference.child(
                        LetsSwtichApplication.applicationContext().getString(
                                R.string.firebase_storage_reference, uid, viewModel.date.value.toDateFormat(
                                FORMAT_YYYY_MM_DDHHMMSS
                        )
                        )
                )

                compress(filePath)?.let { compressResult ->

                    imageReference.putBytes(byteArray)
                            .addOnCompleteListener {

                                imageReference.downloadUrl.addOnCompleteListener { task ->

                                    task.result?.let { taskResult ->

                                        Log.d("Peter", "the result of pic = $taskResult")

                                        viewModel.setPhoto(taskResult)
                                    }
                                }
                            }
                }
            }
        }
    }


    private fun compress(image: Uri): ByteArray? {

        var imageStream: InputStream? = null

        try {
            imageStream =
                    LetsSwtichApplication.applicationContext().contentResolver.openInputStream(
                            image
                    )
        } catch (e: FileNotFoundException) {

            e.printStackTrace()
        }

        val bitmapOrigin = BitmapFactory.decodeStream(imageStream)

        val stream = ByteArrayOutputStream()
        // 縮小至 15 %
        bitmapOrigin.compress(Bitmap.CompressFormat.JPEG, 15, stream)
        val byteArray = stream.toByteArray()

        try {

            stream.close()
            return byteArray
        } catch (e: IOException) {

            e.printStackTrace()
        }

        return null
    }

    private fun getPermissions() {

        val permissions = arrayOf(
                PERMISSION_CAMERA,
                PERMISSION_READ_EXTERNAL_STORAGE,
                PERMISSION_WRITE_EXTERNAL_STORAGE
        )

        when (ContextCompat.checkSelfPermission(
                LetsSwtichApplication.applicationContext(),
                PERMISSION_CAMERA
        )) {

            PackageManager.PERMISSION_GRANTED -> {

                when (ContextCompat.checkSelfPermission(
                        LetsSwtichApplication.applicationContext(),
                        PERMISSION_WRITE_EXTERNAL_STORAGE
                )) {

                    PackageManager.PERMISSION_GRANTED -> {

                        when (ContextCompat.checkSelfPermission(
                                LetsSwtichApplication.applicationContext(),
                                PERMISSION_READ_EXTERNAL_STORAGE
                        )) {

                            PackageManager.PERMISSION_GRANTED -> {

                                isUploadPermissionsGranted = true
                            }
                        }
                    }

                    else -> {
                        ActivityCompat.requestPermissions(
                                activity as MainActivity,
                                permissions,
                                SELECT_PHOTO_PERMISSION_REQUEST_CODE
                        )
                    }
                }
            }

            else -> {
                ActivityCompat.requestPermissions(
                        activity as MainActivity,
                        permissions,
                        SELECT_PHOTO_PERMISSION_REQUEST_CODE
                )
            }
        }

    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {

        isUploadPermissionsGranted = false

        when (requestCode) {

            SELECT_PHOTO_PERMISSION_REQUEST_CODE ->

                if (grantResults.isNotEmpty()) {

                    for (i in 0 until grantResults.size) {

                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                            isUploadPermissionsGranted = false
                            return
                        }
                    }
                    isUploadPermissionsGranted = true
                    try {
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
        }
    }


    private fun startCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(LetsSwtichApplication.applicationContext().packageManager) != null) {

            try {

                fileFromCamera = createImageFile()

                Log.d("EditFragment", "the value of return photo = ${fileFromCamera}")

            } catch (ex: IOException) {
                return
            }
            if (fileFromCamera != null) {
                startActivityForResult(intent, IMAGE_FROM_CAMERA)
            }
        }
    }


    companion object {
        private const val PERMISSION_CAMERA = Manifest.permission.CAMERA
        private const val PERMISSION_WRITE_EXTERNAL_STORAGE =
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val PERMISSION_READ_EXTERNAL_STORAGE =
                Manifest.permission.READ_EXTERNAL_STORAGE
        private const val SELECT_PHOTO_PERMISSION_REQUEST_CODE = 1234

        //Image request code
        private const val IMAGE_FROM_CAMERA = 0
        private const val IMAGE_FROM_GALLERY = 1
        private var chooseCameraOrGallery: String? = null

        //Uri to store the image uri
        private var filePath: Uri? = null

        //Bitmap to get image from gallery
        private var bitmap: Bitmap? = null
        private var auth: FirebaseAuth? = null
        private var fileFromCamera: File? = null
        var isUploadPermissionsGranted = false

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
        } else {
            ActivityCompat.requestPermissions(
                    activity as MainActivity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST
            )
        }

        viewModel.locatMe.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Log.d("ProfileFragment", "value of $it")
                fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient((LetsSwtichApplication.appContext))

                fusedLocationProviderClient.lastLocation.addOnSuccessListener { myLocation ->
                    val newLocation = LatLng(myLocation.latitude, myLocation.longitude)
                    Log.d("MapFragment", "newlocation value = $newLocation")
                    viewModel.mylocation.value = newLocation

                    Log.d("location", "my email = ${UserManager.user.email}")

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
            } else if (it == false) {

                val widthIcon = Resources.getSystem().displayMetrics.widthPixels / 10
                val image = viewModel.userDetail.personImages[0]

                Log.d("Peter", "value of = $image")

                val url = URL(image)
                val position = LatLng(viewModel.userDetail.latitude, viewModel.userDetail.lngti)


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
                            MarkerOptions().position(position)
                                    .icon(
                                            BitmapDescriptorFactory.fromBitmap(
                                                    figureMarker?.let { figure -> createCustomMarker(requireContext(), figure) }
                                            )
                                    )
                    )
                    addMarker.tag = viewModel.userDetail
                }

                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                                position,
                                15.toFloat()
                        )
                )
            }
        })
    }

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