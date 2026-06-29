package com.example.data.repository

import com.example.data.api.MusicApiService
import com.example.data.db.FavoriteSongDao
import com.example.data.model.ArtistInfo
import com.example.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MusicRepository(
    private val apiService: MusicApiService,
    private val favoriteDao: FavoriteSongDao
) {
    suspend fun getHotSongs(): List<Song> {
        val response = apiService.getHotSongs()
        return if (response.success && response.songs != null) {
            response.songs.map { Song.fromDto(it) }
        } else {
            emptyList()
        }
    }

    suspend fun getNewSongs(): List<Song> {
        val response = apiService.getNewSongs()
        return if (response.success && response.songs != null) {
            response.songs.map { Song.fromDto(it) }
        } else {
            emptyList()
        }
    }

    suspend fun getSongs(): List<Song> {
        val response = apiService.getSongs()
        return if (response.success && response.songs != null) {
            response.songs.map { Song.fromDto(it) }
        } else {
            emptyList()
        }
    }

    suspend fun getArtists(): List<ArtistInfo> {
        val response = apiService.getArtists()
        return if (response.success && response.artists != null) {
            response.artists
        } else {
            emptyList()
        }
    }

    suspend fun searchSongs(keyword: String): List<Song> {
        val response = apiService.searchSongs(keyword)
        return if (response.success && response.data != null) {
            response.data.map { Song.fromDto(it) }
        } else {
            emptyList()
        }
    }

    suspend fun getPlayLink(newId: String): String? {
        val response = apiService.getPlayLink(newId)
        return if (response.success) response.data else null
    }

    // Room Favorites
    val allFavorites: Flow<List<Song>> = favoriteDao.getAllFavorites().map { list ->
        list.map { Song.fromFavorite(it) }
    }

    suspend fun addFavorite(song: Song) {
        favoriteDao.insertFavorite(song.toFavoriteEntity())
    }

    suspend fun removeFavorite(newId: String) {
        favoriteDao.deleteFavoriteById(newId)
    }

    fun isFavoriteFlow(newId: String): Flow<Boolean> = favoriteDao.isFavoriteFlow(newId)

    suspend fun isFavorite(newId: String): Boolean = favoriteDao.isFavorite(newId)
}
