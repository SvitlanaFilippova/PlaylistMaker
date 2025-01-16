package com.playlistmaker.domain.models

data class Playlist(
    val id: Int,
    val title: String,
    val description: String?,
    val coverPath: String?,
    val tracks: List<Int>,
    val tracksQuantity: Int
)