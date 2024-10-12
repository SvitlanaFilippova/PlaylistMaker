package com.practicum.playlistmaker.creator


import android.content.Context
import com.practicum.playlistmaker.data.FavoritesStorage
import com.practicum.playlistmaker.data.player.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.search.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.search.TracksRepositoryImpl
import com.practicum.playlistmaker.data.search.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.settings.ThemeRepositoryImpl
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.PlayerRepository
import com.practicum.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.search.HistoryInteractor
import com.practicum.playlistmaker.domain.search.HistoryRepository
import com.practicum.playlistmaker.domain.search.TrackInteractor
import com.practicum.playlistmaker.domain.search.TracksRepository
import com.practicum.playlistmaker.domain.search.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.practicum.playlistmaker.domain.settings.ThemeInteractor
import com.practicum.playlistmaker.domain.settings.ThemeRepository
import com.practicum.playlistmaker.domain.settings.impl.ThemeInteractorImpl

object Creator {
    private lateinit var appContext: Context
    private const val PLAYLISTMAKER_HISTORY_PREFS = "playlistmaker_history_preferences"
    private const val PLAYLISTMAKER_FAVORITES_PREFS = "playlistmaker_favorites_preferences"

    fun init(context: Context) {
        appContext = context
    }

    //for SearchActivity
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(
            RetrofitNetworkClient(appContext),
            FavoritesStorage(
                appContext.getSharedPreferences(
                    PLAYLISTMAKER_FAVORITES_PREFS,
                    Context.MODE_PRIVATE
                )
            )
        )
    }

    fun provideTracksSearchUseCase(): TrackInteractor {
        return TrackInteractorImpl(getTracksRepository())
    }

    private fun getHistoryRepository(): HistoryRepository {
        return HistoryRepositoryImpl(
            appContext.getSharedPreferences(
                PLAYLISTMAKER_HISTORY_PREFS, Context.MODE_PRIVATE
            )
        )
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository())
    }

    //for SettingsActivity
    private fun getThemeRepository(): ThemeRepository {
        return ThemeRepositoryImpl(appContext)
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository())
    }

    //for PlayerActivity
    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(
            getPlayerRepository()
        )
    }

}