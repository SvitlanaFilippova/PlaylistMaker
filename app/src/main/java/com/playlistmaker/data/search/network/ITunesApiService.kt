package com.playlistmaker.data.search.network

import com.playlistmaker.data.search.TracksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {

    @GET("/search?entity=song")
    suspend fun search(@Query("text") text: String): TracksSearchResponse
}



