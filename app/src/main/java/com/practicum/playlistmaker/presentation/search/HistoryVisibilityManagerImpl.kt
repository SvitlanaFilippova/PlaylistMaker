package com.practicum.playlistmaker.presentation.search

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.search.views.HistoryViews

class HistoryVisibilityManagerImpl(
    val historyViews: HistoryViews,
    val tracksAdapter: TrackListAdapter
) : HistoryVisibilityManager {

    @SuppressLint("NotifyDataSetChanged")
    override fun show(searchHistory: ArrayList<Track>) {
        if (searchHistory.isNotEmpty()) {
            historyViews.searchRvResults.isVisible = true
            historyViews.searchBvClearHistory.isVisible = true
            historyViews.searchTvSearchHistory.isVisible = true
            tracksAdapter.setHistoryVisibilityFlag(true)
            tracksAdapter.trackList = searchHistory
            tracksAdapter.notifyDataSetChanged()
        }

    }

    override fun hide() {
        historyViews.searchRvResults.isVisible = false
        historyViews.searchBvClearHistory.isVisible = false
        historyViews.searchTvSearchHistory.isVisible = false
        tracksAdapter.setHistoryVisibilityFlag(false)
    }
}

