package com.playlistmaker.data

import com.playlistmaker.data.db.entity.TrackEntity
import com.playlistmaker.data.search.TrackDto
import com.playlistmaker.domain.Track

import java.text.SimpleDateFormat
import java.util.Locale

fun TrackDto.toDomain(storedFavorites: List<Int>) = Track(
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
        storedFavorites.contains(trackId)
    } else false
)

fun Track.toEntity(timestamp: Long) = TrackEntity(
    trackName,
    artistName,
    trackTime,
    artworkUrl100,
    trackId,
    collectionName,
    releaseDate,
    primaryGenreName,
    country,
    previewUrl,
    coverArtwork,
    timestamp = timestamp
)

fun TrackEntity.toDomain(inFavorite: Boolean) = Track(
    trackName,
    artistName,
    trackTime,
    artworkUrl100,
    trackId,
    collectionName,
    releaseDate,
    primaryGenreName,
    country,
    previewUrl,
    coverArtwork,
    inFavorite
)
