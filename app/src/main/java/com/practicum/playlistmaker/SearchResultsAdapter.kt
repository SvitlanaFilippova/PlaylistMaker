package com.practicum.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivitySearchTrackCardBinding
import java.util.Locale


class SearchResultsAdapter :
    RecyclerView.Adapter<SearchResultsAdapter.SearchResultsHolder>() {
    var trackList: ArrayList<Track> = arrayListOf<Track>()
    lateinit var sharedPreferences: SharedPreferences
    var historyIsVisibleFlag = false
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

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
            if (clickDebounce()) {
                val searchHistory = SearchHistory(sharedPreferences)

                if (trackListSearchHistory.removeIf() { it.trackId == track.trackId }) {
                    if (historyIsVisibleFlag) notifyDataSetChanged()
                }

                if (trackListSearchHistory.size > 9) {
                    trackListSearchHistory.removeAt(9)
                    if (historyIsVisibleFlag) {
                        notifyItemRemoved(9)
                        notifyItemRangeChanged(0, trackListSearchHistory.size - 1)
                    }
                }

                trackListSearchHistory.add(0, track)
                if (historyIsVisibleFlag) {
                    notifyItemInserted(0)
                    notifyItemRangeChanged(0, trackListSearchHistory.size - 1)
                }
                searchHistory.saveHistory(trackListSearchHistory)

                val playerIntent = Intent(holder.parentView.context, PlayerActivity::class.java)
                playerIntent.putExtra("track", Gson().toJson(track)) // Добавляем объект в Intent
                holder.parentView.context.startActivity(playerIntent)
            }
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}
