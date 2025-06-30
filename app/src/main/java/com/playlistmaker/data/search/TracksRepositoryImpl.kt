package com.playlistmaker.data.search


import com.playlistmaker.data.search.network.NetworkClient
import com.playlistmaker.data.toDomain
import com.playlistmaker.data.util.Resource
import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.search.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<ArrayList<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                emit(Resource.Success(ArrayList((response as TracksSearchResponse).results.map {
                    it.toDomain()
                })))
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }


}
