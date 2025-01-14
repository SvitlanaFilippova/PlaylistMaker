package com.playlistmaker.ui.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ListSearchTrackCardBinding


class TrackAdapter(private val onTrackClickDebounce: (track: Track) -> Unit) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    private var trackList: ArrayList<Track> = arrayListOf()


    class TrackViewHolder(private val parentView: View) : RecyclerView.ViewHolder(parentView) {
        private val binding = ListSearchTrackCardBinding.bind(parentView)

        fun bind(track: Track) = with(binding) {
            val cornerRadius =
                parentView.resources.getDimensionPixelSize(R.dimen.search_image_corner_radius_2)
            with(track) {
                searchTvTrackName.text = trackName
                searchTvArtistName.text = artistName
                searchTvTrackTime.text = trackTime

                Glide.with(parentView)
                    .load(artworkUrl100)
                    .centerInside()
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_search_track_card, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { onClickListener(track) }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }


    private fun onClickListener(track: Track) {
        onTrackClickDebounce(track)
    }


    fun submitList(trackList: ArrayList<Track>) {
        this.trackList = trackList
    }
    fun clearList() {
        trackList.clear()
    }

}
