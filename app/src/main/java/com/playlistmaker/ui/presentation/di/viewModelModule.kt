package com.playlistmaker.ui.presentation.di

import com.playlistmaker.domain.models.Track
import com.playlistmaker.ui.library.favorites.FavoritesViewModel
import com.playlistmaker.ui.library.playlists.PlaylistsViewModel
import com.playlistmaker.ui.library.playlists.new_playlist.NewPlaylistViewModel
import com.playlistmaker.ui.library.playlists.playlist.PlaylistViewModel
import com.playlistmaker.ui.player.PlayerViewModel
import com.playlistmaker.ui.search.SearchViewModel
import com.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {

    viewModel<PlayerViewModel> { (track: Track) ->
        PlayerViewModel(
            track = track,
            playerInteractor = get(),
            savedTracksInteractor = get(),
            playlistsInteractor = get(),
            stringProvider = get(),
        )

    }
    viewModel<SearchViewModel> {
        SearchViewModel(
            historyInteractor = get(),
            tracksInteractor = get()
        )
    }
    viewModel<SettingsViewModel> {
        SettingsViewModel(themeInteractor = get())
    }

    viewModel<PlaylistsViewModel> {
        PlaylistsViewModel(playlistsInteractor = get())
    }

    viewModel<FavoritesViewModel> {
        FavoritesViewModel(savedTracksInteractor = get())
    }
    viewModel<NewPlaylistViewModel> {
        NewPlaylistViewModel(
            stringProvider = get(),
            playlistsInteractor = get()
        )
    }
    viewModel<PlaylistViewModel> {
        PlaylistViewModel(
            historyInteractor = get(),
            savedTracksInteractor = get(),
            stringProvider = get(),
            playlistsInteractor = get()
        )
    }

}