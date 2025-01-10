package com.playlistmaker.data.search


import com.playlistmaker.data.search.network.NetworkClient
import com.playlistmaker.data.toDomain
import com.playlistmaker.domain.db.favorites.FavoritesRepository
import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.search.TracksRepository
import com.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val favoritesRepository: FavoritesRepository
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<ArrayList<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                val stored = favoritesRepository.getFavoriteIds()
                emit(Resource.Success(ArrayList((response as TracksSearchResponse).results.map {
                    it.toDomain(stored)
                })))
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }


}
