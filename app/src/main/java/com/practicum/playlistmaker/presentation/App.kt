package com.practicum.playlistmaker.presentation

import android.app.Application
import com.practicum.playlistmaker.Creator


class App : Application() {


    override fun onCreate() {
        Creator.init(applicationContext)
        val themeInteractor = Creator.provideThemeInteractor()
        val isDarkThemeEnabled = themeInteractor.read()
        themeInteractor.switchTheme(isDarkThemeEnabled)

        super.onCreate()


    }


}



