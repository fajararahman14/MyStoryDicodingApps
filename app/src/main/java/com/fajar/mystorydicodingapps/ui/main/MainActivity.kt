package com.fajar.mystorydicodingapps.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajar.mystorydicodingapps.R
import com.fajar.mystorydicodingapps.Result
import com.fajar.mystorydicodingapps.databinding.ActivityMainBinding
import com.fajar.mystorydicodingapps.local.datastore.UserPreference
import com.fajar.mystorydicodingapps.ui.login.LoginActivity
import com.fajar.mystorydicodingapps.ui.maps.MapsActivity
import com.fajar.mystorydicodingapps.ui.story.AddStoryActivity
import com.fajar.mystorydicodingapps.viewmodelfactory.StoryViewModelFactory
import com.fajar.mystorydicodingapps.viewmodelfactory.ViewModelFactory

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupViewModel()
        val storyAdapter = ListStoryAdapter()


        val factory: StoryViewModelFactory = StoryViewModelFactory.getInstance(this)
        val storyViewModel: StoryViewModel by viewModels {
            factory
        }

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                UserPreference.getInstance(dataStore)
            )
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                binding.tvTitle.text = "Welcome,\n${user.name}"

                storyViewModel.getAllStories("Bearer ${user.token}").observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            val storyData = result.data
                            storyAdapter.submitList(storyData)
                        }

                        is Result.Error -> {
                            showLoading(false)
                        }
                    }
                }
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = storyAdapter
        }

    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_story_menu -> {
                Intent(this, AddStoryActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.logout_menu -> {
                mainViewModel.logout()
                finish()
                Intent(this, LoginActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.maps_menu -> {
                Intent(this, MapsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
