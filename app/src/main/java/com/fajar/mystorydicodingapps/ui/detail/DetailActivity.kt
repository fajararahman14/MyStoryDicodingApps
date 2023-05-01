package com.fajar.mystorydicodingapps.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fajar.mystorydicodingapps.R
import com.fajar.mystorydicodingapps.databinding.ActivityDetailBinding
import com.fajar.mystorydicodingapps.network.story.StoryItem
import com.fajar.mystorydicodingapps.utils.withDateFormat

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    private lateinit var binding: ActivityDetailBinding
    private var story : StoryItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        story = intent.getParcelableExtra(EXTRA_DATA)
        getStory()
    }

    private fun getStory(){
        binding.tvTitle.text = story?.name
        binding.tvDescription.text = story?.description
        story?.let { binding.tvCreatedAt.withDateFormat(it.createdAt) }
        Glide.with(this)
            .load(story?.photoUrl)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.ivStory)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}