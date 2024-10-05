package com.practicum.playlistmaker


import android.content.Context
import android.media.MediaPlayer
import android.widget.ImageButton
import android.widget.TextView
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.AgreementRepositoryImpl
import com.practicum.playlistmaker.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.repository.ShareRepositoryImpl
import com.practicum.playlistmaker.data.repository.SupportRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.storage.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.storage.ThemeRepositoryImpl
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
import com.practicum.playlistmaker.presentation.HistoryUpdUseCaseImpl
import com.practicum.playlistmaker.presentation.SettingsActivity

object Creator {
    private lateinit var appContext: Context

    fun init(context: Context) {
        this.appContext = context
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
        buttonPlay: ImageButton,
        tvTrackProgress: TextView,
        mainThreadHandler: android.os.Handler?,
        track: Track
    ): PlayerRepository {
        return PlayerRepositoryImpl(
            mediaPlayer,
            buttonPlay,
            tvTrackProgress,
            mainThreadHandler,
            track
        )
    }

    fun providePlayerInteractor(
        mediaPlayer: MediaPlayer,
        buttonPlay: ImageButton,
        tvTrackProgress: TextView,
        mainThreadHandler: android.os.Handler?,
        track: Track
    ): PlayerInteractor {
        return PlayerInteractorImpl(
            getPlayerRepository(
                mediaPlayer,
                buttonPlay,
                tvTrackProgress,
                mainThreadHandler,
                track
            )
        )
    }

    fun provideMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

}