package com.playlistmaker.ui.search

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.playlistmaker.domain.Track
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchTrackCardBinding


class SearchAdapter(private val onTrackClick: (track: Track) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.SearchResultsHolder>() {
    private var trackList: ArrayList<Track> = arrayListOf()

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())


    class SearchResultsHolder(private val parentView: View) : RecyclerView.ViewHolder(parentView) {
        private val binding = ActivitySearchTrackCardBinding.bind(parentView)

        fun bind(track: Track) = with(binding) {
            val cornerRadius =
                this@SearchResultsHolder.parentView.resources.getDimensionPixelSize(R.dimen.search_image_corner_radius_2)

            searchTvTrackName.text = track.trackName
            searchTvArtistName.text = track.artistName
            searchTvTrackTime.text = track.trackTime

            Glide.with(parentView)
                .load(track.artworkUrl100)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        cornerRadius
                    )
                )
                .placeholder(R.drawable.ic_cover_placeholder)
                .into(searchIvCover)
            searchTvArtistName.requestLayout()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_search_track_card, parent, false)
        return SearchResultsHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultsHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { onClickListener(track) }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    private fun onClickListener(track: Track) {
        if (clickDebounce()) {
            onTrackClick(track)

        }
    }

    fun submitList(trackList: ArrayList<Track>) {
        this.trackList = trackList
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}
