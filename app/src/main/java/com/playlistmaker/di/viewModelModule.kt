package com.playlistmaker.di

import com.playlistmaker.domain.Track
import com.playlistmaker.ui.library.favorites.FavoritesViewModel
import com.playlistmaker.ui.library.playlists.PlaylistsViewModel
import com.playlistmaker.ui.player.PlayerViewModel
import com.playlistmaker.ui.search.SearchViewModel
import com.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel<PlayerViewModel> { (track: Track) ->
        PlayerViewModel(track, playerInteractor = get(), favoritesInteractor = get())

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
        PlaylistsViewModel()
    }

    viewModel<FavoritesViewModel> {
        FavoritesViewModel(favoritesInteractor = get())
    }
}