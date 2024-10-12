package com.playlistmaker.ui

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import com.playlistmaker.creator.Creator


class App : Application() {
    private val themeInteractor by lazy { Creator.provideThemeInteractor() }

    override fun onCreate() {
        super.onCreate()
        Creator.init(applicationContext)
        themeInteractor.switchTheme(checkTheme())
    }

    private fun checkTheme(): Boolean {
        if (!themeInteractor.wasThemeSetManually()) {
            val systemTheme =
                ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
            Log.e(
                "DEBUG",
                "App: Theme wasn't set manually, set system theme as dark theme =$systemTheme"
            )
            return systemTheme

        } else {
            Log.e("DEBUG", "App: Theme was set manually as dark theme =${themeInteractor.read()}")
            return themeInteractor.read()
        }
    }
}



