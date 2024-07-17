package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<SongsResponse>
}

class SongsResponse
    (
    val searchType: String,
    val expression: String,
    val results: List<Track>
)

