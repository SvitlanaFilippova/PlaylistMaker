package com.practicum.playlistmaker

import android.app.Application

import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkTheme = false
    override fun onCreate() {
        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)

        switchTheme(sharedPrefs.getBoolean(THEME_KEY, darkTheme))

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

}

const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"
const val THEME_KEY = "theme_key"


