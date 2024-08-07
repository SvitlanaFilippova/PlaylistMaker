package com.practicum.playlistmaker

import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivitySearchTrackCardBinding
import java.util.Locale


class SearchResultsAdapter :
    RecyclerView.Adapter<SearchResultsAdapter.SearchResultsHolder>() {
    var trackList: ArrayList<Track> = arrayListOf<Track>()
    lateinit var sharedPreferences: SharedPreferences
    var historyIsVisibleFlag = false

    class SearchResultsHolder(val parentView: View) : RecyclerView.ViewHolder(parentView) {
        private val binding = ActivitySearchTrackCardBinding.bind(parentView)


        fun bind(track: Track) = with(binding) {
            val cornerRadius =
                this@SearchResultsHolder.parentView.resources.getDimensionPixelSize(R.dimen.search_image_corner_radius_2)

            searchTvTrackName.text = track.trackName
            searchTvArtistName.text = track.artistName
            searchTvTrackTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
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
        holder.itemView.setOnClickListener {
            if (!historyIsVisibleFlag) {
                val searchHistory = SearchHistory(sharedPreferences)

                trackListSearchHistory.removeIf() { it.trackId == track.trackId }

                if (trackListSearchHistory.size > 9) {
                    trackListSearchHistory.removeAt(9)

                }

                trackListSearchHistory.add(0, track)
                searchHistory.saveHistory(trackListSearchHistory)

            }
        }

    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}
