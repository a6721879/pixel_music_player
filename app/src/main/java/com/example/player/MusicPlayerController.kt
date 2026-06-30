package com.example.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.data.api.RetrofitClient
import com.example.data.model.Song
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException

object MusicPlayerController {
    private var mediaPlayer: MediaPlayer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    // Application context for MediaPlayer headers
    var appContext: android.content.Context? = null

    // Playlist state
    private var currentPlaylist = listOf<Song>()
    private var currentIndex = -1

    // State flows
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setPlaylist(songs: List<Song>, startIndex: Int) {
        currentPlaylist = songs
        currentIndex = startIndex
        if (startIndex in songs.indices) {
            playSong(songs[startIndex])
        }
    }

    fun playSong(song: Song) {
        // Stop current progress tracking
        stopProgressTracker()
        _error.value = null
        _currentSong.value = song
        _isLoading.value = true
        _isPlaying.value = false

        coroutineScope.launch {
            try {
                // Fetch play link from API
                val response = RetrofitClient.apiService.getPlayLink(song.newId)
                if (response.success && !response.data.isNullOrEmpty()) {
                    playUrl(response.data)
                } else {
                    _error.value = "获取播放链接失败或版权受限"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "网络或服务器错误: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    private fun playUrl(url: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                val headers = mapOf(
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
                    "Referer" to "https://tonzhon.whamon.com/",
                    "Origin" to "https://tonzhon.whamon.com"
                )
                val context = appContext
                if (context != null) {
                    setDataSource(context, android.net.Uri.parse(url), headers)
                } else {
                    setDataSource(url)
                }
                setOnPreparedListener { mp ->
                    _isLoading.value = false
                    _isPlaying.value = true
                    _duration.value = mp.duration
                    mp.start()
                    startProgressTracker()
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPosition.value = _duration.value
                    next() // Auto-play next song
                }
                setOnErrorListener { _, what, extra ->
                    _error.value = "播放出错: what=$what, extra=$extra"
                    _isLoading.value = false
                    _isPlaying.value = false
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _error.value = "初始化播放器失败: ${e.localizedMessage}"
            _isLoading.value = false
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer ?: return
        if (mp.isPlaying) {
            mp.pause()
            _isPlaying.value = false
            stopProgressTracker()
        } else {
            if (_currentSong.value != null) {
                mp.start()
                _isPlaying.value = true
                startProgressTracker()
            }
        }
    }

    fun pause() {
        val mp = mediaPlayer ?: return
        if (mp.isPlaying) {
            mp.pause()
            _isPlaying.value = false
            stopProgressTracker()
        }
    }

    fun resume() {
        val mp = mediaPlayer ?: return
        if (!mp.isPlaying && _currentSong.value != null) {
            mp.start()
            _isPlaying.value = true
            startProgressTracker()
        }
    }

    fun next() {
        if (currentPlaylist.isEmpty()) return
        currentIndex = (currentIndex + 1) % currentPlaylist.size
        playSong(currentPlaylist[currentIndex])
    }

    fun previous() {
        if (currentPlaylist.isEmpty()) return
        currentIndex = if (currentIndex - 1 < 0) currentPlaylist.size - 1 else currentIndex - 1
        playSong(currentPlaylist[currentIndex])
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.let { mp ->
            try {
                mp.seekTo(positionMs)
                _currentPosition.value = positionMs
            } catch (e: Exception) {
                // Ignore seek errors
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = coroutineScope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _currentPosition.value = mp.currentPosition
                    }
                }
                delay(300)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracker()
        mediaPlayer?.release()
        mediaPlayer = null
        coroutineScope.cancel()
    }
}
