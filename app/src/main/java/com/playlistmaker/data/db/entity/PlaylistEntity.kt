package com.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String,
    val description: String?,
    val coverPath: String?,
    val trackIdsGson: String,
    val tracksQuantity: Int
)