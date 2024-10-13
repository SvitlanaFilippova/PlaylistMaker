package com.playlistmaker.data.settings

import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.playlistmaker.domain.settings.ThemeRepository


class ThemeRepositoryImpl(private val sharedPreferences: SharedPreferences) : ThemeRepository {

    override fun save(isChecked: Boolean) {
        switchTheme(isChecked)
        sharedPreferences.edit().putBoolean(THEME_KEY, isChecked).apply()
        Log.e("DEBUG", "Сохранил запись про тему в sharedPreferences")
    }

    override fun read(): Boolean {
        val savedInSharedPrefs = sharedPreferences.getBoolean(THEME_KEY, true)

        Log.e(
            "DEBUG",
            "Прочитал запись про тему в sharedPreferences. Сохранённое значение: $savedInSharedPrefs"
        )
        return savedInSharedPrefs
    }

    override fun wasThemeSetManually(): Boolean {
        Log.e(
            "DEBUG", "Theme was set manualy:=${
                sharedPreferences.contains(
                    THEME_KEY
                )
            }"
        )
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
        const val PLAYLISTMAKER_THEME_PREFS = "playlistmaker_theme_preferences"
        const val THEME_KEY = "theme_key"
    }
}





