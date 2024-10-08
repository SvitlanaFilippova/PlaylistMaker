package com.practicum.playlistmaker.util


import android.content.Context
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.AgreementRepositoryImpl
import com.practicum.playlistmaker.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.repository.ShareRepositoryImpl
import com.practicum.playlistmaker.data.repository.SupportRepositoryImpl
import com.practicum.playlistmaker.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.api.IntentRepository
import com.practicum.playlistmaker.domain.api.IntentUseCase
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.api.ThemeInteractor
import com.practicum.playlistmaker.domain.api.ThemeRepository
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.api.TracksSearchUseCase
import com.practicum.playlistmaker.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.IntentUseCaseImpl
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.ThemeInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksSearchUseCaseImpl

object Creator {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context
    }

    //for SearchActivity
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(appContext))
    }

    fun provideTracksSearchUseCase(): TracksSearchUseCase {
        return TracksSearchUseCaseImpl(getTracksRepository())
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

    private fun getIntentRepository(intentType: IntentUseCase.IntentType): IntentRepository {
        return when (intentType) {
            IntentUseCase.IntentType.SHARE -> ShareRepositoryImpl(appContext)
            IntentUseCase.IntentType.SUPPORT ->
                SupportRepositoryImpl(appContext)

            IntentUseCase.IntentType.AGREEMENT -> AgreementRepositoryImpl(appContext)
        }

    }

    fun provideIntentUseCase(intentType: IntentUseCase.IntentType): IntentUseCase {
        return IntentUseCaseImpl(getIntentRepository(intentType))
    }


    // for PlayerActivity
    private fun getPlayerRepository(
        mainThreadHandler: android.os.Handler,
    ): PlayerRepository {
        return PlayerRepositoryImpl(mainThreadHandler)
    }

    fun providePlayerInteractor(
        mainThreadHandler: android.os.Handler,
    ): PlayerInteractor {
        return PlayerInteractorImpl(
            getPlayerRepository(
                mainThreadHandler,

                )
        )
    }


}