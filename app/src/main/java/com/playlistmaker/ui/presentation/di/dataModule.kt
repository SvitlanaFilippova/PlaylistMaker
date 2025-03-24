package com.playlistmaker.ui.presentation.di

import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.playlistmaker.data.SharedPrefsStorage
import com.playlistmaker.data.db.AppDatabase
import com.playlistmaker.data.db.PlaylistsRepositoryImpl
import com.playlistmaker.data.db.SavedTracksRepositoryImpl
import com.playlistmaker.data.player.PlayerRepositoryImpl
import com.playlistmaker.data.search.HistoryRepositoryImpl
import com.playlistmaker.data.search.TracksRepositoryImpl
import com.playlistmaker.data.search.network.ITunesApiService
import com.playlistmaker.data.search.network.NetworkClient
import com.playlistmaker.data.search.network.RetrofitNetworkClient
import com.playlistmaker.data.settings.ThemeRepositoryImpl
import com.playlistmaker.domain.db.playlists.PlaylistsRepository
import com.playlistmaker.domain.db.saved_tracks.SavedTracksRepository
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

    factory<MediaPlayer> {
        MediaPlayer()
    }

    single<Gson> { Gson() }



    single<AppDatabase> {
        Room.databaseBuilder(
            context = get(),
            AppDatabase::class.java, "database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}


val repositoryModule = module {
    single<TracksRepository> {
        TracksRepositoryImpl(networkClient = get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(sharedPrefsStorage = get(), gson = get())
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(sharedPrefsStorage = get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(mediaPlayer = get())
    }
    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(appDatabase = get())
    }

    single<SavedTracksRepository> {
        SavedTracksRepositoryImpl(appDatabase = get())
    }
}
