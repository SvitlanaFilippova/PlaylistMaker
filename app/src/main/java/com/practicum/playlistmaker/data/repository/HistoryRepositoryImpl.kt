package com.practicum.playlistmaker.data.repository
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.models.Track


class HistoryRepositoryImpl(context: Context) : HistoryRepository {
    override val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PLAYLISTMAKER_HISTORY_PREFS, MODE_PRIVATE
    )

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
        const val PLAYLISTMAKER_HISTORY_PREFS = "playlistmaker_history_preferences"
        const val SEARCH_HISTORY_LIST_KEY = "key_for_search_history_list"

    }
}





