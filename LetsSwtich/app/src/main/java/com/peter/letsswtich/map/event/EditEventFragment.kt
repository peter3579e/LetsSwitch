package com.peter.letsswtich.map.event

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.MainActivity
import com.peter.letsswtich.R
import com.peter.letsswtich.databinding.FragmentEditEventBinding
import com.peter.letsswtich.ext.FORMAT_YYYY_MM_DDHHMMSS
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.ext.toDateFormat
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.map.EventPhotoAdapter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class EditEventFragment:Fragment() {

    val viewModel by viewModels<EditEventViewModel> { getVmFactory() }

    private lateinit var binding:FragmentEditEventBinding
    private val AUTOCOMPLETE_REQUEST_CODE = 2
    private val TAG ="EditEventFrgment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditEventBinding.inflate(inflater,container,false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val adapter = EventPhotoAdapter(viewModel)
        binding.photosRecycleView.adapter = adapter

        val photos = mutableListOf<String>("","","","","","","","")

        adapter.submitList(photos)

        binding.locationDetail.setOnClickListener {
            if (!Places.isInitialized()) {
                Places.initialize(
                    LetsSwtichApplication.appContext,
                    getString(R.string.google_maps_key),
                    Locale.TAIWAN
                );
            }
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            val fields =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this.requireContext())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        viewModel.locationName.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.locationName = it
        })

        val cal = Calendar.getInstance()

        binding.createEventTextSelectTime.setOnClickListener {
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            TimePickerDialog(requireContext(), 3,{
                    _, selectHour, selectMinute->
                viewModel.selectedTime.value = String.format("%02d:%02d", selectHour, selectMinute)
                Log.d("MapFragment","date ${viewModel.selectedTime.value}")
                binding.createTime = String.format("%02d:%02d", selectHour, selectMinute)
            }, hour, minute, true).show()

        }
        val textView = binding.createEventTextSelectDate

        textView.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            Log.d("MapFragment","Here has run!!!")
            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            textView.text = sdf.format(cal.time).format(System.currentTimeMillis())
            viewModel.selectedDate.value = sdf.format(cal.time).format(System.currentTimeMillis())
            Log.d("MapFragment","date ${viewModel.selectedDate.value}")

        }

        binding.editTitle.doOnTextChanged { text, start, before, count ->
            viewModel.enterTitle.value = text.toString()
            Log.d("Peter", "${viewModel.enterTitle.value}")
        }

        binding.editDetail.doOnTextChanged { text, start, before, count ->
            viewModel.enterDetail.value = text.toString()
            Log.d("Peter", "${viewModel.enterDetail.value}")
        }

        binding.createEventTextSelectDate.setOnClickListener {

            DatePickerDialog(requireContext(),R.style.DialogTheme, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()

        }


            viewModel.camera.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it == true) {
                    activateCamera()
                    viewModel.closeCamera()
                }
            })



            viewModel.photoUri.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                Log.d("MapFragment", "Photo has run")
                Log.d("MapFragment","value of receive = $it")

                var stop = false

                for (i in 1..photos.size){
                    if (photos[i-1] == "" && !stop){
                        photos[i-1] = it.toString()
                        stop = true
                    }
                }

                viewModel.photoList.value = photos

                Log.d(TAG,"value of photo list = ${viewModel.photoList.value}")

            })

        viewModel.photoList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

                adapter.submitList(viewModel.photoList.value)
                adapter.notifyDataSetChanged()
                Log.d(TAG,"submit list has run !!!!!")
                Log.d("MapFragment","value of newList = ${viewModel.photoList.value}")


        })


        return binding.root
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

        Log.d("Max","run 66")



        //This is the directory in which the file will be created. This is the default location of Camera photos
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ),
            LetsSwtichApplication.applicationContext().getString(R.string.edit_start_camera_camera)
        )
        Log.d("Max","run 77")

        return File.createTempFile(
            viewModel.date.value.toDateFormat(FORMAT_YYYY_MM_DDHHMMSS),  /* prefix */
            LetsSwtichApplication.applicationContext()
                .getString(R.string.edit_start_camera_jpg), /* suffix */
            storageDir      /* directory */
        )
        Log.d("Max","run 88")
    }

    //handling the image chooser activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("MapFragment","request code value = $requestCode")

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            Log.d("MapFragment","Here has run")
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("MapFragment", "Place: ${place.name}, ${place.id}")
                        Log.i("MapFragment", "Place: ${place.latLng.toString()}, ${place.address}")
                        Log.i("MapFragment", "Place: $place")
                        viewModel.locationName.value = place.name


                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("MapFragment", "${status.statusMessage}")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
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

                                    Log.d("Peter", "value of OutBitmap = $outBitmap")

                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                    IMAGE_FROM_CAMERA -> {

                        fileFromCamera?.let {

                            Log.d("Max","the value of file path =  $fileFromCamera")

                            bitmap = data?.extras?.get("data") as Bitmap

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
//                            }
                        }
                    }
                }
            }
        }
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

    private fun uploadFile(bitmap: ByteArray) {



        Log.d("Peter", "Hey1")

        filePath?.let { filePath ->

            Log.d("Peter", "Hey2")

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
                ).child(filePath.toString())




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

                Log.d("EditFragment", "the value of return photo = $fileFromCamera")

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

}