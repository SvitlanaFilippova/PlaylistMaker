package com.practicum.playlistmaker.data.dto

import android.icu.text.SimpleDateFormat
import java.util.Locale

data class TrackDto
    (
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val trackId: Int,
    var collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) {
    fun convertMillisToString(): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    fun getCoverArtwork(): String =
        artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")


}