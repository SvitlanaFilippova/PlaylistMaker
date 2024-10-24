package com.playlistmaker.data

import com.playlistmaker.data.search.TrackDto
import com.playlistmaker.domain.Track

import java.text.SimpleDateFormat
import java.util.Locale

fun TrackDto.toDomain(storedFavorites: Set<String>) = Track(
    trackName ?: "",
    artistName ?: "",
    if (trackTimeMillis !== null) {
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    } else "00:00",
    artworkUrl100 ?: "",
    trackId ?: 0,
    collectionName ?: "",
    if (releaseDate !== null)
        releaseDate.slice(0..3) else "",
    primaryGenreName ?: "",
    country ?: "",
    previewUrl ?: "",
    if (artworkUrl100 !== null) {
        artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    } else "",
    if (trackId !== null) {
        storedFavorites.contains(trackId.toString())
    } else false
)

