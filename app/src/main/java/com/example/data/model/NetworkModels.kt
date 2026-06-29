package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistDto(
    val name: String
)

@JsonClass(generateAdapter = true)
data class AlbumDto(
    val name: String
)

@JsonClass(generateAdapter = true)
data class SongDto(
    val newId: String,
    val name: String,
    val cover: String?,
    val artists: List<ArtistDto>?,
    val album: AlbumDto?,
    val alias: String? = null
)

@JsonClass(generateAdapter = true)
data class SongListResponse(
    val success: Boolean,
    val songs: List<SongDto>?
)

@JsonClass(generateAdapter = true)
data class SafeSearchResponse(
    val success: Boolean,
    val data: List<SongDto>?
)

@JsonClass(generateAdapter = true)
data class PlayLinkResponse(
    val success: Boolean,
    val data: String?
)

@JsonClass(generateAdapter = true)
data class ArtistListResponse(
    val success: Boolean,
    val artists: List<ArtistInfo>?
)

@JsonClass(generateAdapter = true)
data class ArtistInfo(
    val name: String,
    val pic: String?
)
