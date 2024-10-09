package com.practicum.playlistmaker.data.search

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.search.HistoryRepository


class HistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) : HistoryRepository {

    override fun save(history: List<Track>) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_LIST_KEY, Gson().toJson(history))
            .apply()
    }

    override fun read(): ArrayList<Track> {
        val historyJson = sharedPreferences.getString(SEARCH_HISTORY_LIST_KEY, null)
        if (historyJson != null) {
            val itemType = object : TypeToken<List<Track>>() {}.type
            return Gson().fromJson(historyJson, itemType)
        } else return arrayListOf()
    }

    override fun clear() {
        sharedPreferences.edit().remove(SEARCH_HISTORY_LIST_KEY).apply()
    }

    private companion object {
        const val SEARCH_HISTORY_LIST_KEY = "key_for_search_history_list"
    }
}





