package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import androidx.viewbinding.ViewBinding
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.AgreementRepositoryImpl
import com.practicum.playlistmaker.data.repository.ShareRepositoryImpl
import com.practicum.playlistmaker.data.repository.SupportRepositoryImpl
import com.practicum.playlistmaker.data.storage.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.storage.ThemeRepositoryImpl
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.api.IntentRepository
import com.practicum.playlistmaker.domain.api.IntentUseCase
import com.practicum.playlistmaker.domain.api.ThemeInteractor
import com.practicum.playlistmaker.domain.api.ThemeRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.IntentUseCaseImpl
import com.practicum.playlistmaker.domain.impl.ThemeInteractorImpl
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
import com.practicum.playlistmaker.presentation.settings.SettingsActivity

@SuppressLint("StaticFieldLeak")
object Creator {
    private lateinit var context: Context
    private lateinit var binding: ViewBinding

    fun init(context: Context, binding: ViewBinding) {
        this.context = context
        this.binding = binding
    }


    //for SearchActivity
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

    fun providePlaceholderManager(): PlaceholderManager {
        return PlaceholderManagerImpl(
            context = this.context,
            PlaceholderViews(
                (binding as ActivitySearchBinding).searchLlPlaceholder,
                (binding as ActivitySearchBinding).searchIvPlaceholderImage,
                (binding as ActivitySearchBinding).searchTvPlaceholderMessage,
                (binding as ActivitySearchBinding).searchTvPlaceholderExtraMessage,
                (binding as ActivitySearchBinding).searchBvPlaceholderButton,
            )
        )
    }

    fun provideHistoryVisibilityManager(
        tracksAdapter: TrackListAdapter
    ): HistoryVisibilityManager {
        return HistoryVisibilityManagerImpl(
            HistoryViews(
                (binding as ActivitySearchBinding).searchRvResults,
                (binding as ActivitySearchBinding).searchBvClearHistory,
                (binding as ActivitySearchBinding).searchTvSearchHistory
            ), tracksAdapter
        )
    }

    fun provideSearchResultsVisibilityManager(
        tracksAdapter: TrackListAdapter
    ): SearchResultsVisibilityManager {
        return SearchResultsVisibilityManagerImpl(
            SearchResultsViews((binding as ActivitySearchBinding).searchRvResults),
            tracksAdapter
        )
    }


    //for SettingsActivity
    private fun getThemeRepository(): ThemeRepository {
        return ThemeRepositoryImpl(context)
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository())
    }

    private fun getIntentRepository(intentType: SettingsActivity.IntentType): IntentRepository {
        when (intentType) {
            SettingsActivity.IntentType.SHARE -> return ShareRepositoryImpl(context)
            SettingsActivity.IntentType.SUPPORT ->
                return SupportRepositoryImpl(context)

            SettingsActivity.IntentType.AGREEMENT -> return AgreementRepositoryImpl(context)
        }

    }

    fun provideIntentUseCase(intentType: SettingsActivity.IntentType): IntentUseCase {
        return IntentUseCaseImpl(getIntentRepository(intentType))
    }
}