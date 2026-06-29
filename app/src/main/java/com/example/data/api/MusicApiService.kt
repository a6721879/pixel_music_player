package com.example.data.api

import com.example.data.model.ArtistListResponse
import com.example.data.model.PlayLinkResponse
import com.example.data.model.SafeSearchResponse
import com.example.data.model.SongListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicApiService {
    @GET("api/hot-songs")
    suspend fun getHotSongs(): SongListResponse

    @GET("api/new-songs")
    suspend fun getNewSongs(): SongListResponse

    @GET("api/songs")
    suspend fun getSongs(): SongListResponse

    @GET("api/artists")
    suspend fun getArtists(): ArtistListResponse

    @GET("api/ss")
    suspend fun searchSongs(@Query("keyword") keyword: String): SafeSearchResponse

    @GET("api/p/{newId}")
    suspend fun getPlayLink(@Path("newId") newId: String): PlayLinkResponse
}
