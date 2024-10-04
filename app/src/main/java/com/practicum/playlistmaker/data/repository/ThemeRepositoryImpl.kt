package com.practicum.playlistmaker.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.practicum.playlistmaker.domain.api.ThemeRepository
import com.practicum.playlistmaker.presentation.App
import com.practicum.playlistmaker.presentation.THEME_KEY


class ThemeRepositoryImpl(val context: Context) : ThemeRepository {
    override val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE
    )

    override fun save(isChecked: Boolean) {
        (context as App).switchTheme(isChecked)
        sharedPreferences.edit().putBoolean(THEME_KEY, isChecked).apply()
    }


    override fun read(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

}





