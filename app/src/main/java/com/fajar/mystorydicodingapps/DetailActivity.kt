package com.fajar.mystorydicodingapps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fajar.mystorydicodingapps.databinding.ActivityDetailBinding
import com.fajar.mystorydicodingapps.network.story.StoryItem

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

        story = intent.getParcelableExtra(EXTRA_DATA)
    }
}