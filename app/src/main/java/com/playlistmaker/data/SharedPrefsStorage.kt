package com.playlistmaker.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


class SharedPrefsStorage(context: Context) {

    private val historyPrefs =
        context.getSharedPreferences(
            PLAYLISTMAKER_HISTORY_PREFS,
            MODE_PRIVATE
        )

    fun getHistoryPrefs(): SharedPreferences {
        return historyPrefs
    }

    private val themePrefs: SharedPreferences = context.getSharedPreferences(
        PLAYLISTMAKER_THEME_PREFS, MODE_PRIVATE
    )

    fun getThemePrefs(): SharedPreferences {
        return themePrefs
    }


    companion object {
        private const val PLAYLISTMAKER_HISTORY_PREFS = "playlistmaker_new_history_preferences"
        private const val PLAYLISTMAKER_THEME_PREFS = "playlistmaker_theme_preferences"
    }
}