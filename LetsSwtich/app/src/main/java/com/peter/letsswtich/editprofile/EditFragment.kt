package com.peter.letsswtich.editprofile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.peter.letsswtich.LetsSwtichApplication
import com.peter.letsswtich.MainActivity
import com.peter.letsswtich.MainViewModel
import com.peter.letsswtich.R
import com.peter.letsswtich.data.User
import com.peter.letsswtich.databinding.FragmentEditBinding
import com.peter.letsswtich.ext.FORMAT_YYYY_MM_DDHHMMSS
import com.peter.letsswtich.ext.getVmFactory
import com.peter.letsswtich.ext.toDateFormat
import com.peter.letsswtich.login.UserManager
import com.peter.letsswtich.util.Logger
import java.io.*


class EditFragment(user: User) : Fragment() {

    private var userdetail = user

    private lateinit var binding: FragmentEditBinding

    private val viewModel: EditViewModel by viewModels<EditViewModel> { getVmFactory() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        binding = FragmentEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val adapter = EditPhotoAdapter(viewModel)
        binding.photosRecycleView.adapter = adapter

        viewModel.enterMessage.value = userdetail.description

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)


        val list: MutableList<String> = mutableListOf()

        for (user in userdetail.personImages) {
            list.add(user)
        }

        val size = 8 - userdetail.personImages.size


        for (i in 1..size) {
            val nothing = ""
            list.add(nothing)
        }

        viewModel.photoUri.observe(viewLifecycleOwner, Observer {
            val newList = viewModel.photoList.value!!

            Log.d("EditFragment", "the value of newlist = $newList")

            var stop = false

            for (i in 1..newList.size) {
                if (newList[i - 1] == "" && !stop) {
                    newList[i - 1] = it.toString()
                    stop = true
                }
                Log.d("EditFragment", "the value of $i")
            }

            Log.d("EditFragment", "the value of ${UserManager.user.personImages}")

            viewModel.newPhotoList.value = newList
            Log.d("EditFragment", "list after photo uploaded = ${viewModel.photoList.value}")
        })

        viewModel.photoList.value = list

        adapter.submitList(list)

        viewModel.newPhotoList.observe(viewLifecycleOwner, Observer {
            val newSize = 8 - it.size

            for (i in 1..newSize) {
                it!!.add("")
            }

            Log.d("EditFragmnet", "the new list value = $it")

            adapter.submitList(it)

            adapter.notifyDataSetChanged()

            val filteredList = it
            val oldPicList = mainViewModel.userDetail.value
            Log.d("EditFragment", "the old pic list = ${oldPicList!!.personImages}")
            Log.d("EditFragment", "the value of $filteredList")

            val filteredNewList = mutableListOf<String>()
            Log.d("Jason", "Run8")

            if (filteredList[0] == "") {
                Log.d("EditFragment", "the value of $filteredList")
                mainViewModel.userDetail.value!!.personImages = filteredList

            } else {
                for (i in 1..filteredList.size) {
                    if (filteredList[i - 1] != "") {
                        Log.d("EditFramgent", "filter = ${filteredList[i - 1]}")
                        filteredNewList.add(filteredList[i - 1])
                    }
                }
                Log.d("EditFragment", "the value of $filteredList")

                oldPicList.personImages = filteredNewList
                Log.d("EditFragment", "the value of $oldPicList")
                mainViewModel.userDetail.value = oldPicList
            }

        })

        viewModel.camera.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                activateCamera()
                viewModel.closeCamera()
            }
        })

        binding.editText.doOnTextChanged { text, start, before, count ->
            viewModel.enterMessage.value = text.toString()
            mainViewModel.userDetail.value!!.description = viewModel.enterMessage.value!!
            Log.d("Peter", "${viewModel.enterMessage.value}")
        }

        val cityIndicator = LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_city)
        val districtIndicator = LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_district)
        val defaultContent =
                LetsSwtichApplication.instance.resources.getStringArray(R.array.default_array)
        val cityContent =
                LetsSwtichApplication.instance.resources.getStringArray(R.array.city_array)


        //Setup Spinner
        binding.citySpinner.adapter =
                EditSpinnerAdapter(cityContent, cityIndicator)
        binding.districtSpinner.adapter =
                EditSpinnerAdapter(defaultContent, districtIndicator)

        //When city is selected, change the related content of district
        binding.citySpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                    ) {
                        setupDistrictSpinner(pos)

                        if (parent != null && pos != 0) {
                            viewModel.setupCity(parent.selectedItem.toString())
                        }

                    }
                }

        binding.districtSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                    ) {
                        if (parent != null && pos != 0) {
                            viewModel.setupDistrict(parent.selectedItem.toString())
                        }
                    }
                }


        val genderIndicator = userdetail.gender
        val motherTongue = userdetail.fluentLanguage[0]
        val fluentLanguage = userdetail.fluentLanguage[1]
        val genderContent =
                LetsSwtichApplication.instance.resources.getStringArray(R.array.gender_array)
        val languageContent =
                LetsSwtichApplication.instance.resources.getStringArray(R.array.language_array)

        binding.genderSpinner.adapter =
                EditSpinnerAdapter(genderContent, genderIndicator)
        binding.languageSpinner.adapter =
                EditSpinnerAdapter(languageContent, motherTongue)
        binding.secondlanguageSpinner.adapter =
                EditSpinnerAdapter(languageContent, fluentLanguage)

        binding.genderSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                    ) {

                        if (parent != null && pos != 0) {
                            viewModel.setupGender(parent.selectedItem.toString())
                        }

                    }
                }

        binding.languageSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                    ) {

                        if (parent != null && pos != 0) {
                            viewModel.setupMothertongue(parent.selectedItem.toString())
                        }

                    }
                }

        binding.secondlanguageSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                    ) {

                        if (parent != null && pos != 0) {
                            viewModel.setupFluent(parent.selectedItem.toString())
                        }

                    }
                }


        val oldUserDetail = mainViewModel.userDetail.value!!
        val languageList = mutableListOf<String>()


        // Observers
        viewModel.selectedCity.observe(viewLifecycleOwner, Observer {
            Logger.d(it.toString())
            oldUserDetail.city = it
            mainViewModel.userDetail.value = oldUserDetail
        })
        viewModel.selectedDistrict.observe(viewLifecycleOwner, Observer {
            Logger.d(it.toString())
            oldUserDetail.district = it
            mainViewModel.userDetail.value = oldUserDetail
        })

        viewModel.selectedGender.observe(viewLifecycleOwner, Observer {
            Log.d("chose value", "value = $it")
            oldUserDetail.gender = it
            mainViewModel.userDetail.value = oldUserDetail
        })

        viewModel.selectedMothertongue.observe(viewLifecycleOwner, Observer {
            Log.d("chose value", "value = $it")
            languageList.add(it)


        })

        viewModel.selectedFluent.observe(viewLifecycleOwner, Observer {
            Log.d("chose value", "value = $it")
            languageList.add(it)
            Log.d("EditFragment", "the value of = $languageList")
            oldUserDetail.fluentLanguage = languageList
            mainViewModel.userDetail.value = oldUserDetail
        })

        return binding.root
    }


    fun setupDistrictSpinner(pos: Int) {
        binding.districtSpinner.adapter = setSpinnerContent(
                when (pos) {
                    1 -> R.array.taipei_array
                    2 -> R.array.new_taipei_array
                    3 -> R.array.taoyuan_array
                    4 -> R.array.taichung_array
                    5 -> R.array.kaohsiung_array
                    else -> R.array.default_array

                }
        )
    }

    private fun setSpinnerContent(array: Int): android.widget.SpinnerAdapter {
        return EditSpinnerAdapter(
                LetsSwtichApplication.instance.resources.getStringArray(array),
                LetsSwtichApplication.instance.resources.getString(R.string.spinner_select_district)
        )
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

        if (resultCode !== RESULT_CANCELED) {


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

                                    Log.d("Peter", "value of OutBitmap = $outBitmap")

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
//                            }
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

    private fun selectImage() {

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

    private fun uploadFile(bitmap: ByteArray) {

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
                ).child(filePath.toString())

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

                    for (element in grantResults) {

                        if (element != PackageManager.PERMISSION_GRANTED) {

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

    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(LetsSwtichApplication.applicationContext().packageManager) != null) {

            try {
                fileFromCamera = createImageFile()

                Log.d("EditFragment", "the value of return photo = $fileFromCamera")

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
//            private var foodie: Foodie? = null
    }
}