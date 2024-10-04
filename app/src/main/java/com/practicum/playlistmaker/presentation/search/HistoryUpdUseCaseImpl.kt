package com.practicum.playlistmaker.presentation.search

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.HistoryUpdUseCase

class HistoryUpdUseCaseImpl : HistoryUpdUseCase {

    override fun upgrade(
        historyTrackList: ArrayList<Track>,
        historyIsVisible: Boolean,
        position: Int,
        adapter: TrackListAdapter

    ) {
        val track = adapter.trackList[position]

        if (historyTrackList.removeIf { it.trackId == track.trackId }) {
            if (historyIsVisible) {
                adapter.trackList.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }

        if (historyTrackList.size > 9) {
            historyTrackList.removeAt(9)
            if (historyIsVisible) {
                adapter.notifyItemRemoved(9)
                adapter.notifyItemRangeChanged(0, historyTrackList.size - 1)
            }
        }

        historyTrackList.add(0, track)
        if (historyIsVisible) {
            adapter.trackList.add(0, track)
            adapter.notifyItemInserted(0)
            adapter.notifyItemRangeChanged(0, historyTrackList.size - 1)
        }
    }

}