package com.practicum.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.Creator


class App : Application() {

    private var darkTheme = false
    override fun onCreate() {
        Creator.init(applicationContext)
        switchTheme(Creator.provideThemeInteractor().read())

        super.onCreate()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val THEME_KEY = "theme_key"
    }
}



