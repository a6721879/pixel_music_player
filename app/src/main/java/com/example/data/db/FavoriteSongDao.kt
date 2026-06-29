package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteSongDao {
    @Query("SELECT * FROM favorite_songs ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(song: FavoriteSong)

    @Query("DELETE FROM favorite_songs WHERE newId = :newId")
    suspend fun deleteFavoriteById(newId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE newId = :newId LIMIT 1)")
    fun isFavoriteFlow(newId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE newId = :newId LIMIT 1)")
    suspend fun isFavorite(newId: String): Boolean
}
