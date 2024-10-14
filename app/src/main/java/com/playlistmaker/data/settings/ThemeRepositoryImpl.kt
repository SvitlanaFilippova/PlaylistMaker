package com.playlistmaker.data.settings

import androidx.appcompat.app.AppCompatDelegate
import com.playlistmaker.data.SharedPrefsStorage
import com.playlistmaker.domain.settings.ThemeRepository


class ThemeRepositoryImpl(sharedPrefsStorage: SharedPrefsStorage) : ThemeRepository {
    private val sharedPreferences = sharedPrefsStorage.getThemePrefs()


    override fun save(isChecked: Boolean) {
        switchTheme(isChecked)
        sharedPreferences.edit().putBoolean(THEME_KEY, isChecked).apply()
    }

    override fun read(): Boolean {
        val savedInSharedPrefs = sharedPreferences.getBoolean(THEME_KEY, true)
        return savedInSharedPrefs
    }

    override fun wasThemeSetManually(): Boolean {
        return sharedPreferences.contains(THEME_KEY)
    }

    override fun switchTheme(isChecked: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isChecked) {
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





