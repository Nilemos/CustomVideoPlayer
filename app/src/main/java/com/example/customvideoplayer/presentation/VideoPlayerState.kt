package com.example.customvideoplayer.presentation

import android.net.Uri

data class VideoPlayerState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isSeeking: Boolean = false,
    val isBuffering: Boolean = false,
    val isPlayerUiVisible: Boolean = false,
    val selectedVideoUri: Uri? = null,
    val isFullScreen: Boolean = false
)
