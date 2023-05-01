package com.fajar.mystorydicodingapps.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajar.mystorydicodingapps.databinding.ActivityMainBinding
import com.fajar.mystorydicodingapps.local.datastore.UserPreference
import com.fajar.mystorydicodingapps.viewmodelfactory.StoryViewModelFactory
import com.fajar.mystorydicodingapps.viewmodelfactory.ViewModelFactory
import com.fajar.mystorydicodingapps.Result
import com.fajar.mystorydicodingapps.ui.login.LoginActivity

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
        var adapter = ListStoryAdapter()
        adapter.notifyDataSetChanged()



        val factory: StoryViewModelFactory = StoryViewModelFactory.getInstance(this)
        val storyViewModel: StoryViewModel by viewModels {
            factory
        }

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                binding.tvTitle.text = "Welcome,\n${user.name}"

                storyViewModel.getAllStories("Bearer ${user.token}").observe(this) { result ->
                    when(result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            val storyData = result.data
                            adapter.submitList(storyData)
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
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            setAdapter(adapter)
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
                UserPreference.getInstance(dataStore)))[MainViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
