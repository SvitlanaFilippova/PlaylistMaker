package com.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(track: TrackEntity)

    @Query("SELECT * FROM favorites_track_table WHERE isFavorite = 1 AND trackId = :id")
    suspend fun checkIfTrackIsFavorite(id: Int): TrackEntity?


    @Query("SELECT * FROM favorites_track_table  WHERE isFavorite = 1 ORDER BY timestamp DESC")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("SELECT * FROM favorites_track_table WHERE trackId IN (:ids)")
    suspend fun getTracksByIds(ids: List<Int>): List<TrackEntity>


}