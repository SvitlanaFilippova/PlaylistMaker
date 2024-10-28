package com.playlistmaker.di

import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.dsl.module

val uiModule = module {
    factory<LinearLayoutManager> { LinearLayoutManager(get()) }
}