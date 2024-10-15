package com.playlistmaker.di

import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.dsl.module

val uiModule = module {
    single<LinearLayoutManager> { LinearLayoutManager(get()) }
}