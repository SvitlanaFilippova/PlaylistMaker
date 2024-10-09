package com.practicum.playlistmaker.ui

import android.app.Application
import com.practicum.playlistmaker.creator.Creator


class App : Application() {


    override fun onCreate() {
        Creator.init(applicationContext)
        val themeInteractor = Creator.provideThemeInteractor()
        val isDarkThemeEnabled = themeInteractor.read()
        themeInteractor.switchTheme(isDarkThemeEnabled)

        super.onCreate()


    }


}



