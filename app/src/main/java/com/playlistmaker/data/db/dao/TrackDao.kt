package com.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM favorites_track_table ORDER BY timestamp DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favorites_track_table")
    suspend fun getFavoriteIds(): List<Int>

    @Query("SELECT * FROM favorites_track_table WHERE trackId = :id")
    suspend fun getTrackById(id: Int): TrackEntity?
}