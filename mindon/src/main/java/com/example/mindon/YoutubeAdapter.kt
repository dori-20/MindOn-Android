package com.example.mindon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class YoutubeAdapter(
    private val videos: List<YoutubeVideo>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<YoutubeAdapter.YoutubeViewHolder>() {

    inner class YoutubeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.thumbnailImageView)
        val title: TextView = view.findViewById(R.id.titleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video, parent, false)
        return YoutubeViewHolder(view)
    }

    override fun onBindViewHolder(holder: YoutubeViewHolder, position: Int) {
        val video = videos[position]
        holder.title.text = video.title

        Glide.with(holder.itemView.context)
            .load(video.thumbnailUrl)
            .into(holder.thumbnail)

        holder.itemView.setOnClickListener {
            onItemClick(video.videoId)
        }
    }

    override fun getItemCount() = videos.size
}