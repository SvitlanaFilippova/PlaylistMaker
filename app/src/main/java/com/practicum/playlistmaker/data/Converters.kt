package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.search.TrackDto
import com.practicum.playlistmaker.domain.Track

import java.text.SimpleDateFormat
import java.util.Locale

fun TrackDto.toDomain(storedFavorites: Set<String>) = Track(
    trackName,
    artistName,
    SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis),
    artworkUrl100,
    trackId,
    collectionName,
    releaseDate,
    primaryGenreName,
    country,
    previewUrl,
    artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
    inFavorite = storedFavorites.contains(trackId.toString())
)

