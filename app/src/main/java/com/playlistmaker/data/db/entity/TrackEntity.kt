package com.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_track_table")
data class TrackEntity(

    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val trackTime: String,
    val artworkUrl100: String,
    @PrimaryKey
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val coverArtwork: String,
    val isFavorite: Boolean,
    val timestamp: Long
)