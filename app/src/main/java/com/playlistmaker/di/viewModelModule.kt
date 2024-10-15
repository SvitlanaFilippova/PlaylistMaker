package com.playlistmaker.di

import com.playlistmaker.ui.player.view_model.PlayerViewModel
import com.playlistmaker.ui.search.view_model.SearchViewModel
import com.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel<PlayerViewModel> { (trackPreviewUrl: String) ->
        PlayerViewModel(trackPreviewUrl, playerInteractor = get())

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


}