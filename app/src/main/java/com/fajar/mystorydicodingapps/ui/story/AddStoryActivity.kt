package com.fajar.mystorydicodingapps.ui.story


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.fajar.mystorydicodingapps.R
import com.fajar.mystorydicodingapps.databinding.ActivityAddStoryBinding
import com.fajar.mystorydicodingapps.local.datastore.UserPreference
import com.fajar.mystorydicodingapps.network.ApiConfig
import com.fajar.mystorydicodingapps.network.story.UploadFileResponse
import com.fajar.mystorydicodingapps.ui.main.MainActivity
import com.fajar.mystorydicodingapps.ui.main.MainViewModel
import com.fajar.mystorydicodingapps.utils.reduceFileImage
import com.fajar.mystorydicodingapps.utils.rotateBitmap
import com.fajar.mystorydicodingapps.utils.uriToFile
import com.fajar.mystorydicodingapps.viewmodelfactory.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class AddStoryActivity : AppCompatActivity() {


    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val CODE_REQUEST_PERMISSION = 10
    }

    private var getFile: File? = null
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
        binding.apply {
            btnUploadStory.setOnClickListener {
                when {
                    binding.edDescription.text.isEmpty() -> {
                        binding.edDescription.error = getString(R.string.input_description)
                    }
                    else -> {
                        if (getFile != null) {
                            val file = reduceFileImage(getFile as File)
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

                            mainViewModel.getUser().observe(this@AddStoryActivity) { user ->
                                if (user.isLogin) {
                                    val client = ApiConfig.getApiService().uploadStory(
                                        "Bearer ${user.token}",
                                        imageMultipart,
                                        description
                                    )
                                    showLoading(true)
                                    client.enqueue(object : Callback<UploadFileResponse> {
                                        override fun onResponse(
                                            call: Call<UploadFileResponse>,
                                            response: Response<UploadFileResponse>
                                        ) {
                                            if (response.isSuccessful) {
                                                val responseBody = response.body()
                                                if (responseBody != null && !responseBody.error) {
                                                    showLoading(false)
                                                    Toast.makeText(
                                                        this@AddStoryActivity,
                                                        responseBody.message,
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    val intent = Intent(
                                                        this@AddStoryActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                } else {
                                                    showLoading(false)
                                                    Toast.makeText(
                                                        this@AddStoryActivity,
                                                        response.message(),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<UploadFileResponse>,
                                            t: Throwable
                                        ) {
                                            showLoading(false)
                                            Toast.makeText(
                                                this@AddStoryActivity,
                                                getString(R.string.retrofit_error),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
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
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )
            binding.ivStoryPhoto.setImageBitmap(result)
        }
    }


    private fun getAllpermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
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
                    "Access is not Granted",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
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
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}