package com.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.playlistmaker.data.db.dao.PlaylistDao
import com.playlistmaker.data.db.dao.TrackDao
import com.playlistmaker.data.db.entity.PlaylistEntity
import com.playlistmaker.data.db.entity.TrackEntity

@Database(version = 6, entities = [TrackEntity::class, PlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}