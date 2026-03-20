package com.example.customvideoplayer.domain.repository

import com.example.customvideoplayer.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getSelectedVideo(): Flow<Video?>
    suspend fun setSelectedVideo(video: Video?)
}
