package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import androidx.viewbinding.ViewBinding
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.AgreementRepositoryImpl
import com.practicum.playlistmaker.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.repository.ShareRepositoryImpl
import com.practicum.playlistmaker.data.repository.SupportRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.storage.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.storage.ThemeRepositoryImpl
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.api.IntentRepository
import com.practicum.playlistmaker.domain.api.IntentUseCase
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.api.ThemeInteractor
import com.practicum.playlistmaker.domain.api.ThemeRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.IntentUseCaseImpl
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.ThemeInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.HistoryUpdUseCase
import com.practicum.playlistmaker.presentation.player.PlayerTrackDataUpdater
import com.practicum.playlistmaker.presentation.player.PlayerTrackDataUpdaterImpl
import com.practicum.playlistmaker.presentation.search.HistoryUpdUseCaseImpl
import com.practicum.playlistmaker.presentation.search.HistoryVisibilityManager
import com.practicum.playlistmaker.presentation.search.HistoryVisibilityManagerImpl
import com.practicum.playlistmaker.presentation.search.PlaceholderManager
import com.practicum.playlistmaker.presentation.search.PlaceholderManagerImpl
import com.practicum.playlistmaker.presentation.search.SearchResultsVisibilityManager
import com.practicum.playlistmaker.presentation.search.SearchResultsVisibilityManagerImpl
import com.practicum.playlistmaker.presentation.search.TrackListAdapter
import com.practicum.playlistmaker.presentation.search.views.HistoryViews
import com.practicum.playlistmaker.presentation.search.views.PlaceholderViews
import com.practicum.playlistmaker.presentation.search.views.SearchResultsViews
import com.practicum.playlistmaker.presentation.settings.SettingsActivity

@SuppressLint("StaticFieldLeak")
object Creator {
    private lateinit var appContext: Context
    private lateinit var binding: ViewBinding

    fun init(context: Context, binding: ViewBinding) {
        this.appContext = context
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
        return HistoryRepositoryImpl(appContext)
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository())
    }

    fun providePlaceholderManager(): PlaceholderManager {
        return PlaceholderManagerImpl(
            context = this.appContext,
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
        return ThemeRepositoryImpl(appContext)
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository())
    }

    private fun getIntentRepository(intentType: SettingsActivity.IntentType): IntentRepository {
        return when (intentType) {
            SettingsActivity.IntentType.SHARE -> ShareRepositoryImpl(appContext)
            SettingsActivity.IntentType.SUPPORT ->
                SupportRepositoryImpl(appContext)

            SettingsActivity.IntentType.AGREEMENT -> AgreementRepositoryImpl(appContext)
        }

    }

    fun provideIntentUseCase(intentType: SettingsActivity.IntentType): IntentUseCase {
        return IntentUseCaseImpl(getIntentRepository(intentType))
    }


    // for RecycleViewAdapter
    fun provideHistoryUpdUseCase(): HistoryUpdUseCase {
        return HistoryUpdUseCaseImpl()
    }


    // for PlayerActivity
    private fun getPlayerRepository(
        mediaPlayer: MediaPlayer,
        mainThreadHandler: android.os.Handler?,
        track: Track
    ): PlayerRepository {
        return PlayerRepositoryImpl(
            mediaPlayer,
            (binding as ActivityPlayerBinding),
            mainThreadHandler,
            track
        )
    }

    fun providePlayerInteractor(
        mediaPlayer: MediaPlayer,
        mainThreadHandler: android.os.Handler?,
        track: Track
    ): PlayerInteractor {
        return PlayerInteractorImpl(
            getPlayerRepository(
                mediaPlayer, mainThreadHandler, track
            )
        )
    }

    fun providePlayerTrackDataUpdater(): PlayerTrackDataUpdater {
        return PlayerTrackDataUpdaterImpl(binding as ActivityPlayerBinding, appContext)
    }
}