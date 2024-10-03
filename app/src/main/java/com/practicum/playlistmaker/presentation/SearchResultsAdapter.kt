package com.practicum.playlistmaker.presentation

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchTrackCardBinding
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.models.Track


class SearchResultsAdapter(private val historyInteractor: HistoryInteractor) :
    RecyclerView.Adapter<SearchResultsAdapter.SearchResultsHolder>() {
    var trackList: ArrayList<Track> = arrayListOf<Track>()
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
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                val historyTrackList = historyInteractor.read()

                if (historyTrackList.removeIf() { it.trackId == track.trackId }) {
                    if (historyIsVisibleFlag) {
                        trackList.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }

                if (historyTrackList.size > 9) {
                    historyTrackList.removeAt(9)
                    if (historyIsVisibleFlag) {
                        notifyItemRemoved(9)
                        notifyItemRangeChanged(0, historyTrackList.size - 1)
                    }
                }

                historyTrackList.add(0, track)
                if (historyIsVisibleFlag) {
                    trackList.add(0, track)
                    notifyItemInserted(0)
                    notifyItemRangeChanged(0, historyTrackList.size - 1)
                }

                historyInteractor.save(historyTrackList)

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
