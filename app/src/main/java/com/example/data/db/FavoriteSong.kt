package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_songs")
data class FavoriteSong(
    @PrimaryKey val newId: String,
    val name: String,
    val cover: String?,
    val artistName: String,
    val albumName: String?,
    val timestamp: Long = System.currentTimeMillis()
)
