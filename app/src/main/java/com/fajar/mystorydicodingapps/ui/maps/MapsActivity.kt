package com.fajar.mystorydicodingapps.ui.maps

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.fajar.mystorydicodingapps.R
import com.fajar.mystorydicodingapps.databinding.ActivityMapsBinding
import com.fajar.mystorydicodingapps.data.local.datastore.UserPreference
import com.fajar.mystorydicodingapps.network.story.StoryItem
import com.fajar.mystorydicodingapps.ui.main.MainViewModel
import com.fajar.mystorydicodingapps.viewmodelfactory.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    private val mapsViewModel by viewModels<MapsViewModel>()

    private lateinit var mainViewModel: MainViewModel
    private lateinit var listStory: ArrayList<StoryItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMainViewModel()
        listStory = ArrayList()

        supportActionBar?.title = "Maps"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                mapsViewModel.getStoriesLocation(user.token)
            }
        }
        mapsViewModel.mapResult.observe(this) { stories ->
            setupMapData(stories)
            mapFragment.getMapAsync(this)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        for (story in listStory) {
            val storyLocation = LatLng(story.lat.toDouble(), story.lon.toDouble())
            mMap.addMarker(
                MarkerOptions()
                    .position(storyLocation)
                    .title(story.name)
                    .snippet(story.description)
            )
        }



        val jakarta = LatLng(-6.121435, 106.774124)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jakarta, 3f))
        getLocation()
        setMapStyle()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style))

            if (!success) {
                Log.e("MapActivity", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapActivity", "Can't find style. Error: ", e)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLocation()
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setupMainViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                UserPreference.getInstance(dataStore)
            )
        )[MainViewModel::class.java]
    }

    private fun setupMapData(stories: List<StoryItem>) {

        for (story in stories) {
            val storyItem = StoryItem(
                story.id,
                story.name,
                story.description,
                story.photoUrl,
                story.createdAt,
                story.lat,
                story.lon
            )
            listStory.add(storyItem)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}