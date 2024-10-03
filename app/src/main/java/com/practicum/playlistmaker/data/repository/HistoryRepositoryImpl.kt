package com.practicum.playlistmaker.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.models.Track


const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"
const val SEARCH_HISTORY_LIST_KEY = "key_for_search_history_list"


class HistoryRepositoryImpl(context: Context) : HistoryRepository {
    override val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE
    )

    override fun save(history: ArrayList<Track>) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_LIST_KEY, Gson().toJson(history))
            .apply()
    }

    override fun read(): ArrayList<Track> {
        val historyJson = sharedPreferences.getString(SEARCH_HISTORY_LIST_KEY, null)
        if (historyJson != null) {
            val itemType = object : TypeToken<ArrayList<Track>>() {}.type
            return Gson().fromJson(historyJson, itemType)
        } else return arrayListOf()
    }

}





