package com.practicum.playlistmaker.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.settings.ThemeRepository


class ThemeRepositoryImpl(context: Context) : ThemeRepository {
    private var darkTheme = false
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PLAYLISTMAKER_THEME_PREFS, MODE_PRIVATE
    )

    override fun save(isChecked: Boolean) {
        switchTheme(isChecked)
        sharedPreferences.edit().putBoolean(THEME_KEY, isChecked).apply()
    }


    override fun read(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    override fun switchTheme(isChecked: Boolean) {
        darkTheme = isChecked
        AppCompatDelegate.setDefaultNightMode(
            if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES

            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

    }

    private companion object {
        const val PLAYLISTMAKER_THEME_PREFS = "playlistmaker_theme_preferences"
        const val THEME_KEY = "theme_key"
    }
}





