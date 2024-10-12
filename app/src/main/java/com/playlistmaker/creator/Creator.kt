package com.playlistmaker.creator


import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.playlistmaker.data.FavoritesStorage
import com.playlistmaker.data.player.PlayerRepositoryImpl
import com.playlistmaker.data.search.HistoryRepositoryImpl
import com.playlistmaker.data.search.TracksRepositoryImpl
import com.playlistmaker.data.search.network.RetrofitNetworkClient
import com.playlistmaker.data.settings.ThemeRepositoryImpl
import com.playlistmaker.data.settings.ThemeRepositoryImpl.Companion.PLAYLISTMAKER_THEME_PREFS
import com.playlistmaker.domain.player.PlayerInteractor
import com.playlistmaker.domain.player.PlayerRepository
import com.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.playlistmaker.domain.search.HistoryInteractor
import com.playlistmaker.domain.search.HistoryRepository
import com.playlistmaker.domain.search.TrackInteractor
import com.playlistmaker.domain.search.TracksRepository
import com.playlistmaker.domain.search.impl.HistoryInteractorImpl
import com.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.playlistmaker.domain.settings.ThemeInteractor
import com.playlistmaker.domain.settings.ThemeRepository
import com.playlistmaker.domain.settings.impl.ThemeInteractorImpl

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
                    MODE_PRIVATE
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
                PLAYLISTMAKER_HISTORY_PREFS, MODE_PRIVATE
            )
        )
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository())
    }

    //for SettingsActivity
    private fun getThemeRepository(): ThemeRepository {
        val sharedPreferences: SharedPreferences = appContext.getSharedPreferences(
            PLAYLISTMAKER_THEME_PREFS, MODE_PRIVATE
        )
        return ThemeRepositoryImpl(sharedPreferences)
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