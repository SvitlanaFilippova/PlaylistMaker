package com.playlistmaker.di

import com.playlistmaker.domain.db.favorites.FavoritesInteractor
import com.playlistmaker.domain.db.favorites.impl.FavoritesInteractorImpl
import com.playlistmaker.domain.db.playlists.PlaylistsInteractor
import com.playlistmaker.domain.db.playlists.impl.PlaylistsInteractorImpl
import com.playlistmaker.domain.player.PlayerInteractor
import com.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.playlistmaker.domain.search.HistoryInteractor
import com.playlistmaker.domain.search.TrackInteractor
import com.playlistmaker.domain.search.impl.HistoryInteractorImpl
import com.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.playlistmaker.domain.settings.ThemeInteractor
import com.playlistmaker.domain.settings.impl.ThemeInteractorImpl
import org.koin.dsl.module

val domainModule = module {
    factory<TrackInteractor> {
        TrackInteractorImpl(repository = get())
    }

    factory<HistoryInteractor> {
        HistoryInteractorImpl(repository = get())
    }

    factory<ThemeInteractor> {
        ThemeInteractorImpl(repository = get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(repository = get())
    }

    factory<FavoritesInteractor> {
        FavoritesInteractorImpl(repository = get())
    }

    factory<PlaylistsInteractor> {
        PlaylistsInteractorImpl(repository = get())
    }
}





