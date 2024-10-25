package com.playlistmaker.di

import android.media.MediaPlayer
import com.google.gson.Gson
import com.playlistmaker.data.FavoritesStorage
import com.playlistmaker.data.SharedPrefsStorage
import com.playlistmaker.data.player.PlayerRepositoryImpl
import com.playlistmaker.data.search.HistoryRepositoryImpl
import com.playlistmaker.data.search.TracksRepositoryImpl
import com.playlistmaker.data.search.network.ITunesApiService
import com.playlistmaker.data.search.network.NetworkClient
import com.playlistmaker.data.search.network.RetrofitNetworkClient
import com.playlistmaker.data.settings.ThemeRepositoryImpl
import com.playlistmaker.domain.player.PlayerRepository
import com.playlistmaker.domain.search.HistoryRepository
import com.playlistmaker.domain.search.TracksRepository
import com.playlistmaker.domain.settings.ThemeRepository
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(context = get(), iTunesService = get())
    }

    single<SharedPrefsStorage> {
        SharedPrefsStorage(context = get())
    }
    single<FavoritesStorage> {
        FavoritesStorage(sharedPrefsStorage = get())
    }
    single<TracksRepository> {
        TracksRepositoryImpl(networkClient = get(), favoritesStorage = get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(sharedPrefsStorage = get(), gson = get())
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(sharedPrefsStorage = get())
    }

    factory<MediaPlayer> {
        MediaPlayer()
    }

    single<Gson> { Gson() }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(mediaPlayer = get(), favoritesStorage = get())
    }
}

