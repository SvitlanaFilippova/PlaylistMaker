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

fun TrackDto.toDomain() = Track(
    trackName = trackName ?: "",
    artistName = artistName ?: "",
    trackTimeMillis = trackTimeMillis ?: 0,
    trackTime = trackTimeMillis?.let {
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(it)
    } ?: "00:00",
    artworkUrl100 = artworkUrl100 ?: "",
    trackId = trackId ?: 0,
    collectionName = collectionName ?: "",
    releaseDate = releaseDate?.take(4).orEmpty(),
    primaryGenreName = primaryGenreName ?: "",
    country = country ?: "",
    previewUrl = previewUrl ?: "",
    coverArtwork = if (artworkUrl100 !== null) {
        artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    } else "",
    inFavorite = null
)

fun Track.toEntity(timestamp: Long) = TrackEntity(
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    trackTime = trackTime,
    artworkUrl100 = artworkUrl100,
    trackId = trackId,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    coverArtwork = coverArtwork,
    isFavorite = inFavorite ?: false,
    timestamp = timestamp
)

fun TrackEntity.toDomain() = Track(
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    trackTime = trackTime,
    artworkUrl100 = artworkUrl100,
    trackId = trackId,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    coverArtwork = coverArtwork,
    inFavorite = isFavorite
)

fun Playlist.toEntity() = PlaylistEntity(
    id = id,
    title = title,
    description = description,
    coverPath = coverPath,
    trackIdsGson = Gson().toJson(tracks),
    tracksQuantity = tracks.size,

    )

fun PlaylistEntity.toDomain() = Playlist(
    id = id,
    title = title,
    description = description,
    coverPath = coverPath,
    tracks = Gson().fromJson(trackIdsGson, object : TypeToken<List<Int>>() {}.type),
    tracksQuantity = tracksQuantity
)


