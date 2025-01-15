package com.playlistmaker.data.search

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.playlistmaker.data.SharedPrefsStorage
import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.search.HistoryRepository


class HistoryRepositoryImpl(sharedPrefsStorage: SharedPrefsStorage, private val gson: Gson) :
    HistoryRepository {
    private val sharedPreferences = sharedPrefsStorage.getHistoryPrefs()

    override fun save(history: List<Track>) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_LIST_KEY, gson.toJson(history))
            .apply()
    }

    override fun read(): ArrayList<Track> {
        val historyJson = sharedPreferences.getString(SEARCH_HISTORY_LIST_KEY, null)
        if (historyJson != null) {
            val itemType = object : TypeToken<List<Track>>() {}.type
            return gson.fromJson(historyJson, itemType)
        } else return arrayListOf()
    }

    override fun clear() {
        sharedPreferences.edit().remove(SEARCH_HISTORY_LIST_KEY).apply()
    }

    private companion object {
        const val SEARCH_HISTORY_LIST_KEY = "key_for_search_history_list"
    }
}





