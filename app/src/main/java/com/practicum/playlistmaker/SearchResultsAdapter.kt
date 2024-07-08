package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class SearchResultsAdapter : RecyclerView.Adapter<SearchResultsAdapter.SearchResultsHolder>() {

    class SearchResultsHolder(private val parentView: View) : RecyclerView.ViewHolder(parentView) {
        private var coverImageView: ImageView =
            parentView.findViewById<ImageView>(R.id.search_iv_cover)
        private var trackNameTextView: TextView =
            parentView.findViewById<TextView>(R.id.search_tv_track_name)
        private var artistNameTextView: TextView =
            parentView.findViewById<TextView>(R.id.search_tv_artist_name)
        private var trackTimeTextView: TextView =
            parentView.findViewById<TextView>(R.id.search_tv_track_time)

        fun bind(track: Track) {
            val cornerRadius = this.parentView.resources.getDimensionPixelSize(R.dimen.search_image_corner_radius_2)

            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            trackTimeTextView.text = track.trackTime
            Glide.with(parentView)
                .load(track.artworkUrl100)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        cornerRadius
                    )
                )
                .placeholder(R.drawable.ic_cover_placeholder)
                .into(coverImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_search_track_card, parent, false)
        return SearchResultsHolder(view)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: SearchResultsHolder, position: Int) {
        holder.bind(trackList[position])
    }
}