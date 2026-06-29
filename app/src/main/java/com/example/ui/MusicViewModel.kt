package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.RetrofitClient
import com.example.data.db.MusicDatabase
import com.example.data.model.ArtistInfo
import com.example.data.model.Song
import com.example.data.repository.MusicRepository
import com.example.player.MusicPlayerController
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MusicDatabase.getDatabase(application)
    private val repository = MusicRepository(RetrofitClient.apiService, database.favoriteSongDao())

    // UI States
    private val _hotSongsState = MutableStateFlow<UiState<List<Song>>>(UiState.Loading)
    val hotSongsState: StateFlow<UiState<List<Song>>> = _hotSongsState.asStateFlow()

    private val _newSongsState = MutableStateFlow<UiState<List<Song>>>(UiState.Loading)
    val newSongsState: StateFlow<UiState<List<Song>>> = _newSongsState.asStateFlow()

    private val _recommendedSongsState = MutableStateFlow<UiState<List<Song>>>(UiState.Loading)
    val recommendedSongsState: StateFlow<UiState<List<Song>>> = _recommendedSongsState.asStateFlow()

    private val _artistsState = MutableStateFlow<UiState<List<ArtistInfo>>>(UiState.Loading)
    val artistsState: StateFlow<UiState<List<ArtistInfo>>> = _artistsState.asStateFlow()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResultsState = MutableStateFlow<UiState<List<Song>>?>(null)
    val searchResultsState: StateFlow<UiState<List<Song>>?> = _searchResultsState.asStateFlow()

    // Favorites
    val favoriteSongs: StateFlow<List<Song>> = repository.allFavorites
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Player States from controller
    val currentSong: StateFlow<Song?> = MusicPlayerController.currentSong
    val isPlaying: StateFlow<Boolean> = MusicPlayerController.isPlaying
    val currentPosition: StateFlow<Int> = MusicPlayerController.currentPosition
    val duration: StateFlow<Int> = MusicPlayerController.duration
    val isPlayerLoading: StateFlow<Boolean> = MusicPlayerController.isLoading
    val playerError: StateFlow<String?> = MusicPlayerController.error

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _hotSongsState.value = UiState.Loading
            _newSongsState.value = UiState.Loading
            _recommendedSongsState.value = UiState.Loading
            _artistsState.value = UiState.Loading

            // Load Hot Songs
            launch {
                try {
                    val songs = repository.getHotSongs()
                    _hotSongsState.value = UiState.Success(songs)
                } catch (e: Exception) {
                    _hotSongsState.value = UiState.Error(e.localizedMessage ?: "加载热门歌曲失败")
                }
            }

            // Load New Songs
            launch {
                try {
                    val songs = repository.getNewSongs()
                    _newSongsState.value = UiState.Success(songs)
                } catch (e: Exception) {
                    _newSongsState.value = UiState.Error(e.localizedMessage ?: "加载最新歌曲失败")
                }
            }

            // Load Recommended Songs
            launch {
                try {
                    val songs = repository.getSongs()
                    _recommendedSongsState.value = UiState.Success(songs)
                } catch (e: Exception) {
                    _recommendedSongsState.value = UiState.Error(e.localizedMessage ?: "加载推荐歌曲失败")
                }
            }

            // Load Artists
            launch {
                try {
                    val artists = repository.getArtists()
                    _artistsState.value = UiState.Success(artists)
                } catch (e: Exception) {
                    _artistsState.value = UiState.Error(e.localizedMessage ?: "加载热门歌手失败")
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.trim().isEmpty()) {
            _searchResultsState.value = null
        }
    }

    fun performSearch() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) return

        _searchResultsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val results = repository.searchSongs(query)
                _searchResultsState.value = UiState.Success(results)
            } catch (e: Exception) {
                _searchResultsState.value = UiState.Error(e.localizedMessage ?: "搜索失败，请重试")
            }
        }
    }

    fun playSong(song: Song, playlist: List<Song>) {
        val index = playlist.indexOfFirst { it.newId == song.newId }
        val finalIndex = if (index != -1) index else 0
        val finalPlaylist = if (index != -1) playlist else playlist + song
        MusicPlayerController.setPlaylist(finalPlaylist, finalIndex)
    }

    fun togglePlayPause() {
        MusicPlayerController.togglePlayPause()
    }

    fun next() {
        MusicPlayerController.next()
    }

    fun previous() {
        MusicPlayerController.previous()
    }

    fun seekTo(positionMs: Int) {
        MusicPlayerController.seekTo(positionMs)
    }

    fun isFavorite(songId: String): Flow<Boolean> {
        return repository.isFavoriteFlow(songId)
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            val isFav = repository.isFavorite(song.newId)
            if (isFav) {
                repository.removeFavorite(song.newId)
            } else {
                repository.addFavorite(song)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Do not release singleton controller here so it lives through activity recreations,
        // but we can release it when the app process is terminated.
    }
}
