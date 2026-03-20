package com.example.customvideoplayer.domain.usecase

import com.example.customvideoplayer.domain.model.Video
import com.example.customvideoplayer.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow

class GetSelectedVideoUseCase(
    private val repository: VideoRepository
) {
    operator fun invoke(): Flow<Video?> = repository.getSelectedVideo()
}
