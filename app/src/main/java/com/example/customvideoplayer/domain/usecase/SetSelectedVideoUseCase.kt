package com.example.customvideoplayer.domain.usecase

import com.example.customvideoplayer.domain.model.Video
import com.example.customvideoplayer.domain.repository.VideoRepository

class SetSelectedVideoUseCase(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(video: Video?) = repository.setSelectedVideo(video)
}
