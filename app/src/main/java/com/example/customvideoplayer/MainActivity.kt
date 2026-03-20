package com.example.customvideoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.customvideoplayer.data.repository.VideoRepositoryImpl
import com.example.customvideoplayer.domain.usecase.GetSelectedVideoUseCase
import com.example.customvideoplayer.domain.usecase.SetSelectedVideoUseCase
import com.example.customvideoplayer.presentation.MediaPickerScreen
import com.example.customvideoplayer.presentation.VideoPlayerViewModel
import com.example.customvideoplayer.ui.theme.CustomVideoPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Manual DI for demonstration purposes
        val repository = VideoRepositoryImpl()
        val getSelectedVideoUseCase = GetSelectedVideoUseCase(repository)
        val setSelectedVideoUseCase = SetSelectedVideoUseCase(repository)
        
        setContent {
            CustomVideoPlayerTheme {
                val viewModel: VideoPlayerViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return VideoPlayerViewModel(
                                application = application,
                                getSelectedVideoUseCase = getSelectedVideoUseCase,
                                setSelectedVideoUseCase = setSelectedVideoUseCase
                            ) as T
                        }
                    }
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0)
                ) { innerPadding ->
                    MediaPickerScreen(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}
