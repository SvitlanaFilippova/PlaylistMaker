package com.playlistmaker.ui.presentation


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ListPlaylistCardBinding

class PlaylistAdapter(private val viewType: Int, private val onItemClick: (Playlist) -> Unit) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {
    private var playlists: ArrayList<Playlist> = arrayListOf()


    class PlaylistViewHolder(
        private val parentView: View,
    ) : RecyclerView.ViewHolder(parentView) {
        private val binding = ListPlaylistCardBinding.bind(parentView)
        fun bind(playlist: Playlist) = binding.apply {

            with(playlist) {
                tvTitle.text = title
                tvQuantity.text =
                    parentView.context.getString(
                        R.string.quantity_of_tracks_template,
                        tracksQuantity,
                        getTracksText(tracksQuantity)
                    )
                val cover = if (coverPath.isNullOrEmpty()) R.drawable.ic_placeholder_with_foreground
                else coverPath

                Glide.with(parentView)
                    .load(cover)
                    .into(ivCover as ImageView)
            }
        }

        private fun getTracksText(quantity: Int): String {
            val remainder10 = quantity % 10
            val remainder100 = quantity % 100
            val trackEnding =
                when {
                    remainder100 in 11..19 -> "ов"// Числа от 11 до 19 всегда "треков"
                    remainder10 == 1 -> ""// Числа, оканчивающиеся на 1 (кроме 11), "трек"
                    remainder10 in 2..4 -> "а" // Числа, оканчивающиеся на 2, 3, 4 (кроме 12–14), "трека"
                    else -> "ов"  // Всё остальное — "треков"
                }
            return parentView.context.getString(R.string.track_template, trackEnding)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layout = if (this.viewType == MEDIUM_PLAYLISTS_GRID) {
            R.layout.list_playlist_card
        } else {
            R.layout.list_small_playlist_card
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener { onItemClick(playlist) }

    }


    fun submitList(playlists: ArrayList<Playlist>) {
        this.playlists = playlists
    }

    fun clearList() {
        playlists.clear()
    }


    companion object {
        const val MEDIUM_PLAYLISTS_GRID = 1
        const val SMALL_PLAYLISTS_LIST = 2

    }
}