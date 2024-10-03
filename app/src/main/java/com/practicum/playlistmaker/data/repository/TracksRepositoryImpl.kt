package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override var resultCode = 0
    override fun searchTracks(expression: String): ArrayList<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        resultCode = response.resultCode
        if (resultCode == 200) {
            return ArrayList((response as TracksSearchResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis),
                    it.artworkUrl100,
                    it.trackId,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl,
                    it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
                )
            })

        } else {
            return arrayListOf()
        }
    }
}