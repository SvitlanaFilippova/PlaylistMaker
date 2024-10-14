package com.playlistmaker.data.search

import com.playlistmaker.data.FavoritesStorage
import com.playlistmaker.data.search.network.NetworkClient
import com.playlistmaker.data.toDomain
import com.playlistmaker.domain.Track
import com.playlistmaker.domain.search.TracksRepository
import com.practicum.playlistmaker.util.Resource
import com.practicum.playlistmaker.util.Resource.Success

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val favoritesStorage: FavoritesStorage
) : TracksRepository {

    override fun searchTracks(expression: String): Resource<ArrayList<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                val stored = favoritesStorage.getSavedFavorites()
                Success(ArrayList((response as TracksSearchResponse).results.map {
                    it.toDomain(stored)
                }))
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }


}
