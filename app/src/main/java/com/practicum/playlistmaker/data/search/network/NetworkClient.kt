package com.practicum.playlistmaker.data.search.network

import com.practicum.playlistmaker.data.search.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}