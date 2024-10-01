package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.models.Track

class SearchHistory(private val sharedPreferences: SharedPreferences) {


    fun saveHistory(tracks: ArrayList<Track>) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_LIST_KEY, Gson().toJson(tracks))
            .apply()
    }


    fun readHistory() {
        val searchHistoryString = sharedPreferences.getString(SEARCH_HISTORY_LIST_KEY, null)
        if (searchHistoryString != null) {
            trackListSearchHistory = createTrackListFromJson(searchHistoryString)
        }
    }

    private fun createTrackListFromJson(json: String): ArrayList<Track> {
        val itemType = object : TypeToken<kotlin.collections.ArrayList<Track>>() {}.type
        return Gson().fromJson<kotlin.collections.ArrayList<Track>>(json, itemType)
    }

    fun clearHistory() {
        trackListSearchHistory.clear()
        sharedPreferences.edit().remove(SEARCH_HISTORY_LIST_KEY).apply()

    }

}

var trackListSearchHistory = arrayListOf<Track>()
const val SEARCH_HISTORY_LIST_KEY = "key_for_search_history_list"