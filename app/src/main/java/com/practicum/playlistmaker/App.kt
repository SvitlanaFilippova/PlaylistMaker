package com.practicum.playlistmaker

import android.app.Application

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.data.storage.PLAYLISTMAKER_PREFERENCES

const val THEME_KEY = "theme_key"

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



