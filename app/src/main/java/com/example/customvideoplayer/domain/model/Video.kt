package com.example.customvideoplayer.domain.model

import android.net.Uri

data class Video(
    val uri: Uri,
    val name: String = "Selected Video"
)
