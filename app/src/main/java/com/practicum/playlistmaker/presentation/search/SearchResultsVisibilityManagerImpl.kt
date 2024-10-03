package com.practicum.playlistmaker.presentation.search

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.search.views.SearchResultsViews

class SearchResultsVisibilityManagerImpl(
    private val searchResultsViews: SearchResultsViews,
    private val tracksAdapter: TrackListAdapter
) : SearchResultsVisibilityManager {

    @SuppressLint("NotifyDataSetChanged")
    override fun show(foundTracks: ArrayList<Track>): Boolean {
        searchResultsViews.searchRvResults.isVisible = true
        tracksAdapter.trackList = foundTracks
        tracksAdapter.notifyDataSetChanged()
        var searchResultsIsVisible = true
        return searchResultsIsVisible
    }


    override fun hide(): Boolean {
        return false
    }
}
