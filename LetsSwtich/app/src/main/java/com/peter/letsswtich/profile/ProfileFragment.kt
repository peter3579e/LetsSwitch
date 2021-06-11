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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isGone
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
import com.peter.letsswtich.data.StoreLocation
import com.peter.letsswtich.databinding.FragmentProfileBinding
import com.peter.letsswtich.dialog.MatchedDialogArgs
import com.peter.letsswtich.dialog.MatchedDialogViewModel
import com.peter.letsswtich.editprofile.EditFragment
import com.peter.letsswtich.ext.FORMAT_YYYY_MM_DDHHMMSS
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.ext.toDateFormat
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.map.createCustomMarker
import kotlinx.coroutines.*
import java.io.*
import java.net.URL


class ProfileFragment : Fragment() , GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

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
            if(viewModel.userDetail.fluentLanguage.isNotEmpty()){
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

            if (viewModel.userDetail.preferLanguage.isNotEmpty()){
                for (language in viewModel.userDetail.preferLanguage) {
                    val learningChip = binding.chipLearning
                    val chip3 = Chip(learningChip.context)
                    chip3.text = language
                    learningChip.addView(chip3)
                }
            }

            Log.d("ProfileFragment", "valeu of navigation = ${mainViewModel.navigateToFriendsProfile.value}")
            binding.buttonBack.setOnClickListener {
                if (viewModel.ifMap == true){
                    findNavController().navigate(NavigationDirections.navigateToMapFragment())
                }else{
                    findNavController().navigate(NavigationDirections.navigateToChatroomFragment(viewModel.userDetail.email, viewModel.userDetail.name,false))
                }
            }

        }else{
            if(UserManager.user.fluentLanguage.isNotEmpty()){
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

            if (UserManager.user.preferLanguage.isNotEmpty()){
                for (language in viewModel.userDetail.preferLanguage) {
                    val learningChip = binding.chipLearning
                    val chip3 = Chip(learningChip.context)
                    chip3.text = language
                    learningChip.addView(chip3)
                }
            }
        }

        viewModel.picDialog.observe(viewLifecycleOwner, Observer {
            if(it == true){
                findNavController().navigate(NavigationDirections.navigateToPicDialog(viewModel.userDetail.personImages[0]))
            }else if (it == false){
                findNavController().navigate(NavigationDirections.navigateToPicDialog(viewModel.userDetail.backGroundPic))
            }
        })



        viewModel.navigateToEditProfile.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().navigate(NavigationDirections.navigateToEditProfilePage(viewModel.userDetail))
                Log.d("ProfileFragment","the value of = ${viewModel.userDetail}")
                viewModel.editProfileNavigated()
            }
        })

        viewModel.setting.observe(viewLifecycleOwner, Observer {
            if (it == true){
                findNavController().navigate(NavigationDirections.navigateToSettingFragment(mainViewModel.requirment.value!!))
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
            Log.d("profileFragment","Uri has received")
            viewModel.userDetail.backGroundPic = it.toString()
            binding.backgroundImage = viewModel.userDetail.backGroundPic
            Log.d("profileFragment","Uri has received $it")
            mainViewModel.userdetail.value!!.backGroundPic = it.toString()
            UserManager.user.backGroundPic = it.toString()
            Log.d("profileFragment","User with background ${mainViewModel.userdetail.value!!.backGroundPic}")
            Log.d("profileFragment","UserManager with background ${UserManager.user.backGroundPic}")
            viewModel.updateUser(mainViewModel.userdetail.value!!)

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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode !== Activity.RESULT_CANCELED) {


            if (resultCode == Activity.RESULT_OK) {

                when (requestCode) {
                    IMAGE_FROM_GALLERY -> {

                        Log.d("Peter", "Run1")

                        data?.let {

                            Log.d("Peter", "Run2")

                            it.data?.let { data ->

                                Log.d("Peter", "Run3")

                                filePath = data
                                Log.d("Peter", "Run4")

                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(
                                        (activity as MainActivity).contentResolver, filePath
                                    )
                                    Log.d("Peter", "Run5")

                                    val matrix = Matrix()
                                    Log.d("Peter", "Run6")
                                    matrix.postRotate(
                                        getImageRotation(
                                            LetsSwtichApplication.applicationContext(),
                                            data
                                        ).toFloat()
                                    )
                                    Log.d("Peter", "Run7")

                                    val outBitmap = Bitmap.createBitmap(
                                        bitmap!!, 0, 0,
                                        bitmap!!.width, bitmap!!.height, matrix, false
                                    )
                                    Log.d("Peter", "Run8")

                                    val byte = ByteArrayOutputStream()

                                    outBitmap.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            15,
                                            byte
                                    )

                                    val byteArray = byte.toByteArray()



                                    uploadFile(byteArray)

//                                    scalePic(outBitmap, 100)

//                                    val bitmapToString = BitMaptoString(outBitmap)
//
//                                    Log.d("Peter","BitMapToString = $bitmapToString")
//
//                                    viewModel.newPhotoList.value!!.add(bitmapToString)
//
//                                    Log.d("Peter","value of photolist = ${viewModel.newPhotoList.value}")

//                                    binding.foodiePhoto.setImageBitmap(outBitmap)

                                    Log.d("Peter", "Run10")

                                } catch (e: IOException) {
                                    Log.d("Peter", "Run11")
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                   IMAGE_FROM_CAMERA -> {

                        fileFromCamera?.let {
                            Log.d("Max","Run1")

                            Log.d("Max","the value of file path =  $fileFromCamera")

                            Log.d("Max","Run2")



                                Log.d("Max","Run1")

                                bitmap = data?.extras?.get("data") as Bitmap
//                                    MediaStore.Images.Media.getBitmap(
//                                        (activity as MainActivity).contentResolver,
//                                        it
//                                    )
                            Log.d("Max","$bitmap")
                                Log.d("Max","Run2")

                                val matrix = Matrix()

                                Log.d("Max","Run3")

                                Log.d("Max","Run4")

                                val outBitmap = Bitmap.createBitmap(
                                    bitmap!!, 0, 0,
                                    bitmap!!.width, bitmap!!.height, matrix, false
                                )

                                Log.d("Max","Run5")

                                Log.d("Max","Run6")
                                val byte = ByteArrayOutputStream()

                                Log.d("Max","Run7")
                                outBitmap.compress(
                                        Bitmap.CompressFormat.JPEG,
                                        15,
                                        byte
                                )

                                Log.d("Max","Run8")

                                val byteArray = byte.toByteArray()

                                Log.d("Max","Run9")

                                uploadCamera(byteArray)
//                            }
                        }
                    }
                }
            }
        }
    }

//    private fun scalePic(bitmap: Bitmap, phone: Int)
//    {
//        Log.d("Peter","Wow111x")
//        //縮放比例預設為1
//        var scaleRate = 1f
//        Log.d("Peter","Wow1")
//
//        //如果圖片寬度大於手機寬度則進行縮放，否則直接將圖片放入ImageView內
//        if(bitmap.width > phone) {
//            Log.d("Peter","Wow2")
//
//
//            scaleRate = phone.toFloat()/ bitmap.width.toFloat() //判斷縮放比例
//
//            Log.d("Peter","Wow3")
//
//            val matrix = Matrix()
//            matrix.setScale(scaleRate, scaleRate)
//
//            Log.d("Peter","Wow4")
//
//            binding.foodiePhoto.setImageBitmap(
//                Bitmap.createBitmap(
//                    bitmap, 0, 0,
//                    bitmap.width, bitmap.height, matrix, false
//                )
//            )
//            Log.d("Peter","Wow5")
//        }
//        else binding.foodiePhoto.setImageBitmap(bitmap)
//    }

    fun BitMaptoString(bitmap: Bitmap): String {
        Log.d("Peter", "Yes1")
        val ByteStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ByteStream)
        Log.d("Peter", "Yes2")
        val b = ByteStream.toByteArray()
        Log.d("Peter", "Yes3")
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun getImageRotation(context: Context, uri: Uri): Int {
        var stream: InputStream? = null
        return try {
            Log.d("Peter", "Ya1")
            stream = context.contentResolver.openInputStream(uri)
            Log.d("Peter", "Ya2")
            val exifInterface = ExifInterface(stream!!)
            Log.d("Peter", "Ya3")
            val exifOrientation =
                exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            Log.d("Peter", "Ya4")
            when (exifOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            Log.d("Peter", "Ya6")
            0
        } finally {
            Log.d("Peter", "Ya7")
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



        Log.d("Peter", "Hey1")


        Log.d("Peter", "Hey2")

        viewModel.uploadPhoto()

        Log.d("Peter", "Hey3")

        UserManager.uid?.let { uid ->

            Log.d("Peter", "Hey4")

            // Firebase storage
            auth = FirebaseAuth.getInstance()

            Log.d("Peter", "Hey5")

            Log.d("Peter", "the value of date = ${viewModel.date.value}")

            val imageReference = FirebaseStorage.getInstance().reference.child(
                    LetsSwtichApplication.applicationContext().getString(
                            R.string.firebase_storage_reference, uid, viewModel.date.value.toDateFormat(
                            FORMAT_YYYY_MM_DDHHMMSS
                    )
                    )
            ).child(fileFromCamera.toString())




            Log.d("Peter", "Hey6")

            Log.d("Peter", "Hey7")

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

        Log.d("Peter", "Hey1")

        filePath?.let { filePath ->

            Log.d("Peter", "Hey2")

            viewModel.uploadPhoto()

            Log.d("Peter", "Hey3")

            UserManager.uid?.let { uid ->

                Log.d("Peter", "Hey4")

                // Firebase storage
                auth = FirebaseAuth.getInstance()

                Log.d("Peter", "Hey5")

                Log.d("Peter", "the value of date = ${viewModel.date.value}")

                val imageReference = FirebaseStorage.getInstance().reference.child(
                    LetsSwtichApplication.applicationContext().getString(
                        R.string.firebase_storage_reference, uid, viewModel.date.value.toDateFormat(
                            FORMAT_YYYY_MM_DDHHMMSS
                        )
                    )
                )

                Log.d("Peter", "Hey6")

                compress(filePath)?.let { compressResult ->

                    Log.d("Peter", "Hey7")

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

    fun getPermissions() {

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

        Log.d("Max","run 123")

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        Log.d("Max","$intent")
        Log.d("Max","run 1234")
        if (intent.resolveActivity(LetsSwtichApplication.applicationContext().packageManager) != null) {

            Log.d("Max","run 12")

            try {
                Log.d("Max","run 13")
                fileFromCamera = createImageFile()

                Log.d("EditFragment", "the value of return photo = ${fileFromCamera}")

            } catch (ex: IOException) {
                Log.d("Max","run 14")
                return
            }
            if (fileFromCamera != null) {
                Log.d("Max","run 15")
                startActivityForResult(intent, IMAGE_FROM_CAMERA)
                Log.d("Max","run 18")
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
        private var displayMetrics: DisplayMetrics? = null
        private var windowManager: WindowManager? = null
        private var fileFromCamera: File? = null
        var isUploadPermissionsGranted = false
//            private var foodie: Foodie? = null
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

        viewModel.locatMe.observe(viewLifecycleOwner, Observer {
            if (it == true){
                Log.d("ProfileFragment","value of $it")
                fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient((LetsSwtichApplication.appContext))

                fusedLocationProviderClient.lastLocation.addOnSuccessListener { myLocation ->
                    val newLocation = LatLng(myLocation.latitude, myLocation.longitude)
                    Log.d("MapFragment","Has run here!")
                    Log.d("MapFragment", "newlocation value = $newLocation")
                    viewModel.mylocation.value = newLocation

                    Log.d("location","my email = ${UserManager.user.email}")


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
            }else if (it == false){
                Log.d("Map Fragment","Map has run!!")

                    val widthIcon = Resources.getSystem().displayMetrics.widthPixels / 10
                    val image = viewModel.userDetail.personImages[0]

                    Log.d("Peter","value of = $image")

                    val url  = URL(image)
                    val position = LatLng(viewModel.userDetail.latitude,viewModel.userDetail.lngti)


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
                                    createCustomMarker(requireContext(), figureMarker!!, "Narender")
                                )
                            )
                    )
                    addMarker.tag = viewModel.userDetail
                }


//                    GlobalScope.launch(Dispatchers.Main) {
//                        // show bitmap on image view when available
//                        val figureMarker = result.await()?.let { it1 ->
//                            Bitmap.createScaledBitmap(
//                                    it1, widthIcon, widthIcon, false)
//                        }

//                        val addMarker = googleMap.addMarker(
//                                MarkerOptions().position(position)
//                                        .flat(true)
////                                    .alpha(0.5F)
////                                .icon(iconDraw)
//                                        .icon(BitmapDescriptorFactory.fromBitmap(figureMarker))
//                        )
//                        addMarker.tag = viewModel.userDetail
//
//                    }

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
            val storeLocation = (it.tag as StoreLocation)
            val store = storeLocation.store
        }
        return false
    }

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