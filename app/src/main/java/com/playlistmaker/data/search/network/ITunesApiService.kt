package com.playlistmaker.data.search.network

import com.playlistmaker.data.search.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TracksSearchResponse>
}



