package com.fajar.mystorydicodingapps.ui.story


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.fajar.mystorydicodingapps.R
import com.fajar.mystorydicodingapps.Result
import com.fajar.mystorydicodingapps.databinding.ActivityAddStoryBinding
import com.fajar.mystorydicodingapps.data.local.datastore.UserPreference
import com.fajar.mystorydicodingapps.ui.main.MainActivity
import com.fajar.mystorydicodingapps.ui.main.MainViewModel
import com.fajar.mystorydicodingapps.utils.reduceFileImage
import com.fajar.mystorydicodingapps.utils.rotateBitmap
import com.fajar.mystorydicodingapps.utils.uriToFile
import com.fajar.mystorydicodingapps.viewmodelfactory.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class AddStoryActivity : AppCompatActivity() {


    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val CODE_REQUEST_PERMISSION = 10
    }

    private var getFile: File? = null
    private var myLocation: Location? = null
    private val addStoryViewModel by viewModels<AddStoryViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val supportActionBar = supportActionBar
        supportActionBar?.title = ""
        supportActionBar?.elevation = 0f

        setupViewModel()

        if (!getAllpermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                CODE_REQUEST_PERMISSION
            )
        }
        addStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }


        binding.btnCamera.setOnClickListener {
            val intent = Intent(this@AddStoryActivity, CameraActivity::class.java)
            launcherCameraX.launch(intent)
        }

        binding.btnGallery.setOnClickListener {
            val intent = Intent()
            intent.action = ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Pilih gambar")
            launcherGallery.launch(chooser)
        }
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@AddStoryActivity)
        getMyLocation()

        binding.apply {
            btnUploadStory.setOnClickListener {
                when {
                    binding.edDescription.text.isEmpty() -> {
                        binding.edDescription.error = getString(R.string.input_description)
                    }
                    else -> {
                        if (getFile != null) {
                            val file = getFile as File
                            if (file.length() > 1000000) {
                                reduceFileImage(file)
                            }
                            val description = binding.edDescription.text.toString()
                                .toRequestBody("text/plain".toMediaType())
                            val requestImageFile =
                                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            val imageMultipart: MultipartBody.Part =
                                MultipartBody.Part.createFormData(
                                    "photo",
                                    file.name,
                                    requestImageFile
                                )
                            val lat = myLocation?.latitude?.toFloat()
                            val lon = myLocation?.longitude?.toFloat()

                            mainViewModel.getUser().observe(this@AddStoryActivity) { user ->
                                if (user.isLogin) {
                                    addStoryViewModel.addStory(
                                        token = user.token,
                                        photo = imageMultipart,
                                        description = description,
                                        lat = lat,
                                        lon = lon
                                    )
                                    addStoryViewModel.addStoryResult.observe(this@AddStoryActivity) { result ->
                                        when (result) {
                                            is Result.Loading -> showLoading(true)

                                            is Result.Success -> {
                                                Toast.makeText(
                                                    this@AddStoryActivity,
                                                    resources.getString(R.string.add_story),
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                val intent = Intent(
                                                    this@AddStoryActivity,
                                                    MainActivity::class.java
                                                )
                                                startActivity(intent)
                                            }

                                            is Result.Error -> {
                                                Toast.makeText(
                                                    this@AddStoryActivity,
                                                    resources.getString(R.string.add_string_failed),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        this@AddStoryActivity,
                                        getString(R.string.insert_your_picture),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile
            binding.ivStoryPhoto.setImageURI(selectedImg)
        }
    }

    private val launcherCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CAMERA_X_RESULT) {
            val myFile = result.data?.getSerializableExtra("picture") as? File
            val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                getFile = file
                val resultBitmap =
                    rotateBitmap(BitmapFactory.decodeFile(getFile?.path), isBackCamera)
                binding.ivStoryPhoto.setImageBitmap(resultBitmap)
            }
        } else {
            @Suppress("DEPRECATION")
            result.data?.getSerializableExtra("picture") as? File
        }
    }


    private fun getAllpermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(
            baseContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CODE_REQUEST_PERMISSION) {
            if (!getAllpermissionGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.access_not_granted),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLocation()
            }

            else -> {
                binding.swLocation.isChecked = false
                binding.swLocation.isEnabled = false
            }
        }
    }

    private fun getMyLocation() {
        if (
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    myLocation = it
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.enable_location),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(
            baseContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                UserPreference.getInstance(dataStore)
            )
        )[MainViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }
}