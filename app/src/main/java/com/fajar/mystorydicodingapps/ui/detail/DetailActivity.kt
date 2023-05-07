package com.fajar.mystorydicodingapps.ui.detail

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fajar.mystorydicodingapps.R
import com.fajar.mystorydicodingapps.Result
import com.fajar.mystorydicodingapps.databinding.ActivityDetailBinding
import com.fajar.mystorydicodingapps.local.datastore.UserPreference
import com.fajar.mystorydicodingapps.network.story.StoryItem
import com.fajar.mystorydicodingapps.ui.main.MainViewModel
import com.fajar.mystorydicodingapps.utils.withDateFormat
import com.fajar.mystorydicodingapps.viewmodelfactory.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
    private lateinit var binding: ActivityDetailBinding
    private lateinit var mainViewModel: MainViewModel
    private val detailViewModel by viewModels<DetailViewModel>()

    private var story: StoryItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMainViewModel()
        setupActionBar()
        getIntentExtras()
        setupObservers()
    }

    private fun setupActionBar() {
        supportActionBar?.title = ""
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getIntentExtras() {
        story = intent.getParcelableExtra(EXTRA_DATA)
        story?.let { setStoryDetails(it) }
    }

    private fun setupObservers() {
        detailViewModel.isLoading.observe(this) { showLoading(it) }
        mainViewModel.getUser().observe(this) { user -> user?.token?.let { detailViewModel.detailStory(it, story?.id.orEmpty()) } }
        detailViewModel.storyResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> setStoryDetails(result.data.story)
                is Result.Error -> showError(result.error)
            }
        }
    }

    private fun setStoryDetails(story: StoryItem) {
        val tvName = binding.tvTitle
        val tvDate = binding.tvCreatedAt
        val tvDescription = binding.tvDescription
        val image = binding.ivStory

        tvName.text = story.name
        tvDate.withDateFormat(story.createdAt)
        tvDescription.text = story.description

        Glide.with(binding.root)
            .load(story.photoUrl)
            .placeholder(R.drawable.ic_replay)
            .into(image)
    }

    private fun showError(errorMessage: String?) {
        Toast.makeText(
            this,
            errorMessage ?: resources.getString(R.string.detail_status),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setupMainViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                UserPreference.getInstance(dataStore)
            )
        )[MainViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}