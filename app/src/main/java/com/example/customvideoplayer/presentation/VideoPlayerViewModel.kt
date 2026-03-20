package com.example.customvideoplayer.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.customvideoplayer.domain.model.Video
import com.example.customvideoplayer.domain.usecase.GetSelectedVideoUseCase
import com.example.customvideoplayer.domain.usecase.SetSelectedVideoUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VideoPlayerViewModel(
    application: Application,
    private val getSelectedVideoUseCase: GetSelectedVideoUseCase,
    private val setSelectedVideoUseCase: SetSelectedVideoUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(VideoPlayerState())
    val uiState = _uiState.asStateFlow()

    val player = ExoPlayer.Builder(application).build().apply {
        addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _uiState.update { 
                    it.copy(
                        isBuffering = playbackState == Player.STATE_BUFFERING,
                        duration = if (playbackState == Player.STATE_READY) duration.coerceAtLeast(0) else it.duration
                    )
                }
            }
        })
    }

    init {
        viewModelScope.launch {
            getSelectedVideoUseCase().collect { video ->
                video?.let {
                    _uiState.update { state -> state.copy(selectedVideoUri = it.uri) }
                    player.setMediaItem(MediaItem.fromUri(it.uri))
                    player.prepare()
                    player.play()
                }
            }
        }

        viewModelScope.launch {
            while (true) {
                if (_uiState.value.isPlaying && !_uiState.value.isSeeking) {
                    _uiState.update { it.copy(currentPosition = player.currentPosition.coerceAtLeast(0)) }
                }
                delay(16L)
            }
        }
    }

    fun onVideoSelected(video: Video) {
        viewModelScope.launch {
            setSelectedVideoUseCase(video)
        }
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_ENDED) {
                player.seekTo(0)
            }
            player.play()
        }
    }

    fun onSeeking(position: Long) {
        _uiState.update { it.copy(isSeeking = true, currentPosition = position) }
    }

    fun onSeekFinished(position: Long) {
        player.seekTo(position)
        _uiState.update { it.copy(isSeeking = false) }
    }

    fun toggleUiVisibility() {
        _uiState.update { it.copy(isPlayerUiVisible = !it.isPlayerUiVisible) }
        if (_uiState.value.isPlayerUiVisible) {
            viewModelScope.launch {
                delay(5000)
                if (!_uiState.value.isSeeking) {
                    _uiState.update { it.copy(isPlayerUiVisible = false) }
                }
            }
        }
    }

    fun toggleFullScreen() {
        _uiState.update { it.copy(isFullScreen = !it.isFullScreen) }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
