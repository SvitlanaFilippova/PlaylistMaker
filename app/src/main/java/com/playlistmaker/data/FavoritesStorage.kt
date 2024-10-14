package com.playlistmaker.data

import android.content.SharedPreferences

class FavoritesStorage(private val sharedPreferences: SharedPreferences) {


    fun addToFavorites(trackId: Int) {
        changeFavorites(trackId = trackId.toString(), remove = false)
    }

    fun removeFromFavorites(trackId: Int) {
        changeFavorites(trackId = trackId.toString(), remove = true)
    }

    fun getSavedFavorites(): Set<String> {
        return sharedPreferences.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
    }

    private fun changeFavorites(trackId: String, remove: Boolean) {
        val mutableSet = getSavedFavorites().toMutableSet()
        val modified = if (remove) mutableSet.remove(trackId) else mutableSet.add(trackId)
        if (modified) sharedPreferences.edit().putStringSet(FAVORITES_KEY, mutableSet).apply()
    }

    private companion object {
        const val FAVORITES_KEY = "FAVORITES_KEY"
    }

}