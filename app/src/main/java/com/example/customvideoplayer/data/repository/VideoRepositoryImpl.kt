package com.example.customvideoplayer.data.repository

import com.example.customvideoplayer.domain.model.Video
import com.example.customvideoplayer.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VideoRepositoryImpl : VideoRepository {
    private val _selectedVideo = MutableStateFlow<Video?>(null)

    override fun getSelectedVideo(): Flow<Video?> = _selectedVideo.asStateFlow()

    override suspend fun setSelectedVideo(video: Video?) {
        _selectedVideo.value = video
    }
}
