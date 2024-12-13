package com.playlistmaker.data.search.network

import com.playlistmaker.data.search.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response

}