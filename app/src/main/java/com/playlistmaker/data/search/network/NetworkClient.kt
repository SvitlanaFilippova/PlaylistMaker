package com.playlistmaker.data.search.network

import com.playlistmaker.data.search.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}