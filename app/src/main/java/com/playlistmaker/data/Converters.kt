package com.playlistmaker.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.playlistmaker.data.db.entity.PlaylistEntity
import com.playlistmaker.data.db.entity.TrackEntity
import com.playlistmaker.data.search.TrackDto
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.models.Track

import java.text.SimpleDateFormat
import java.util.Locale

fun TrackDto.toDomain(storedFavorites: List<Int>) = Track(
    trackName = trackName ?: "",
    artistName = artistName ?: "",
    trackTime = if (trackTimeMillis !== null) {
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    } else "00:00",
    artworkUrl100 = artworkUrl100 ?: "",
    trackId = trackId ?: 0,
    collectionName = collectionName ?: "",
    releaseDate = if (releaseDate !== null)
        releaseDate.slice(0..3) else "",
    primaryGenreName = primaryGenreName ?: "",
    country = country ?: "",
    previewUrl = previewUrl ?: "",
    coverArtwork = if (artworkUrl100 !== null) {
        artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    } else "",
    inFavorite = if (trackId !== null) {
        storedFavorites.contains(trackId)
    } else false
)

fun Track.toEntity(timestamp: Long) = TrackEntity(
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl100 = artworkUrl100,
    trackId = trackId,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    coverArtwork = coverArtwork,
    timestamp = timestamp
)

fun TrackEntity.toDomain(inFavorite: Boolean) = Track(
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl100 = artworkUrl100,
    trackId = trackId,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    coverArtwork = coverArtwork,
    inFavorite = inFavorite
)

fun Playlist.toEntity() = PlaylistEntity(
    title = title,
    description = description,
    coverPath = coverPath,
    trackIdsGson = Gson().toJson(tracks),
    tracksQuantity = tracksQuantity
)

fun PlaylistEntity.toDomain() = Playlist(
    id = id,
    title = title,
    description = description,
    coverPath = coverPath,
    tracks = Gson().fromJson(trackIdsGson, object : TypeToken<List<String>>() {}.type),
    tracksQuantity = tracksQuantity
)
