package com.practicum.playlistmaker.data.Impl

import com.practicum.playlistmaker.data.FavoritesStorage
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.data.toDomain
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
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
