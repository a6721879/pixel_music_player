package com.example.data.model

import com.example.data.db.FavoriteSong

data class Song(
    val newId: String,
    val name: String,
    val cover: String?,
    val artistName: String,
    val albumName: String?
) {
    fun toFavoriteEntity(): FavoriteSong {
        return FavoriteSong(
            newId = newId,
            name = name,
            cover = cover,
            artistName = artistName,
            albumName = albumName
        )
    }

    companion object {
        fun fromDto(dto: SongDto): Song {
            val artistStr = dto.artists?.joinToString(", ") { it.name } ?: "未知歌手"
            return Song(
                newId = dto.newId,
                name = dto.name,
                cover = dto.cover,
                artistName = artistStr,
                albumName = dto.album?.name ?: "未知专辑"
            )
        }

        fun fromFavorite(entity: FavoriteSong): Song {
            return Song(
                newId = entity.newId,
                name = entity.name,
                cover = entity.cover,
                artistName = entity.artistName,
                albumName = entity.albumName
            )
        }
    }
}
