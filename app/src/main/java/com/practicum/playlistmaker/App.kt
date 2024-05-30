package com.practicum.playlistmaker

import android.app.Application

import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false
    override fun onCreate() {
        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        val darkThemeWasEnabled = sharedPrefs.getString(THEME_KEY, "false")


        if (darkThemeWasEnabled != null) {
            when (darkThemeWasEnabled) {
                "true" -> switchTheme(true)
                "false" -> switchTheme(false)
            }
        }




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


