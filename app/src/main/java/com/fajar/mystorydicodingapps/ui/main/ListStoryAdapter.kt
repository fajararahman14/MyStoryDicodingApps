package com.fajar.mystorydicodingapps.ui.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fajar.mystorydicodingapps.R
import com.fajar.mystorydicodingapps.databinding.IvItemStoryBinding
import com.fajar.mystorydicodingapps.network.story.StoryItem
import com.fajar.mystorydicodingapps.ui.detail.DetailActivity
import com.fajar.mystorydicodingapps.utils.withDateFormat


class ListStoryAdapter : ListAdapter<StoryItem, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = IvItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val listStory = getItem(position)
        holder.bind(listStory)
        val imgPhoto: ImageView = holder.itemView.findViewById(R.id.iv_story)
        val tvName: TextView = holder.itemView.findViewById(R.id.tv_name)
        val tvDate: TextView = holder.itemView.findViewById(R.id.tv_created_at)
        val tvDescription: TextView = holder.itemView.findViewById(R.id.tv_description)

        holder.itemView.setOnClickListener {
            val data = StoryItem(
                listStory.id,
                listStory.name,
                listStory.description,
                listStory.photoUrl,
                listStory.createdAt,
                listStory.lat,
                listStory.lon
            )
            val intent = Intent(it.context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_DATA, data)
            val optionCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    Pair(imgPhoto, "image_story"),
                    Pair(tvName, "name"),
                    Pair(tvDate, "time"),
                    Pair(tvDescription, "description")
                )
            it.context.startActivity(intent, optionCompat.toBundle())
        }
    }
    class ListViewHolder(private val binding: IvItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryItem) {
            binding.tvName.text = story.name
            binding.tvDescription.text = story.description
            binding.tvCreatedAt.withDateFormat(story.createdAt)
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .apply(RequestOptions())
                .into(binding.ivStory)
        }


    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryItem> =
            object : DiffUtil.ItemCallback<StoryItem>() {
                override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
