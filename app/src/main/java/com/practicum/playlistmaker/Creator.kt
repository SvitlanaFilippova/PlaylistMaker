package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.storage.HistoryRepositoryImpl
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.presentation.search.HistoryVisibilityManagerImpl
import com.practicum.playlistmaker.presentation.search.PlaceholderManager
import com.practicum.playlistmaker.presentation.search.PlaceholderManagerImpl
import com.practicum.playlistmaker.presentation.search.TrackListAdapter
import com.practicum.playlistmaker.presentation.search.views.HistoryViews
import com.practicum.playlistmaker.presentation.search.views.PlaceholderViews
import com.practicum.playlistmaker.presentation.search.HistoryVisibilityManager
import com.practicum.playlistmaker.presentation.search.SearchResultsVisibilityManager
import com.practicum.playlistmaker.presentation.search.SearchResultsVisibilityManagerImpl
import com.practicum.playlistmaker.presentation.search.views.SearchResultsViews

@SuppressLint("StaticFieldLeak")
object Creator {
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }


    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getHistoryRepository(): HistoryRepository {
        return HistoryRepositoryImpl(context)
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository())
    }

    fun providePlaceholderManager(
        searchLlPlaceholder: View,
        searchIvPlaceholderImage: ImageView,
        searchTvPlaceholderMessage: TextView,
        searchTvPlaceholderExtraMessage: TextView,
        searchBvPlaceholderButton: Button,
    ): PlaceholderManager {
        return PlaceholderManagerImpl(
            context = this.context,
            PlaceholderViews(
                searchLlPlaceholder,
                searchIvPlaceholderImage,
                searchTvPlaceholderMessage,
                searchTvPlaceholderExtraMessage,
                searchBvPlaceholderButton,

                )
        )
    }

    fun provideHistoryVisibilityManager(
        searchRvResults: RecyclerView,
        searchBvClearHistory: Button,
        searchTvSearchHistory: TextView,
        tracksAdapter: TrackListAdapter,
    ): HistoryVisibilityManager {
        return HistoryVisibilityManagerImpl(
            HistoryViews(
                searchRvResults,
                searchBvClearHistory,
                searchTvSearchHistory
            ), tracksAdapter
        )
    }

    fun provideSearchResultsVisibilityManager(
        searchRvResults: RecyclerView,
        tracksAdapter: TrackListAdapter
    ): SearchResultsVisibilityManager {
        return SearchResultsVisibilityManagerImpl(
            SearchResultsViews(searchRvResults),
            tracksAdapter
        )
    }
}