package com.example.customvideoplayer.presentation

import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.ui.compose.ContentFrame
import com.example.customvideoplayer.domain.model.Video

@Composable
fun MediaPickerScreen(
    viewModel: VideoPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.onVideoSelected(Video(uri = it))
        }
    }

    val activity = context as? ComponentActivity

    LaunchedEffect(uiState.isFullScreen) {
        val window = activity?.window ?: return@LaunchedEffect
        val view = window.decorView
        val controller = WindowCompat.getInsetsController(window, view)

        if (uiState.isFullScreen) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    Column(
        modifier = modifier
            .background(Color.DarkGray)
            .fillMaxSize()
            .then(if (!uiState.isFullScreen) Modifier.statusBarsPadding() else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = if (uiState.isFullScreen) {
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            } else {
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            }
        ) {
            ContentFrame(
                player = viewModel.player,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {
                        if (uiState.selectedVideoUri == null) {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.VideoOnly
                                )
                            )
                        } else {
                            viewModel.toggleUiVisibility()
                        }
                    }
            )

            if (uiState.selectedVideoUri == null) {
                Text(
                    text = "Tap to select video",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AnimatedVisibility(
                    visible = uiState.isPlayerUiVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    PlayerUi(
                        isPlaying = uiState.isPlaying,
                        isBuffering = uiState.isBuffering,
                        isSeeking = uiState.isSeeking,
                        currentPosition = uiState.currentPosition,
                        duration = uiState.duration,
                        isFullScreen = uiState.isFullScreen,
                        onFullScreenClick = {
                            viewModel.toggleFullScreen()
                        },
                        onSeekBarPositionChange = {
                            viewModel.onSeeking(it)
                        },
                        onSeekBarPositionChangeFinished = {
                            viewModel.onSeekFinished(it)
                        },
                        onPlayPauseClick = {
                            viewModel.togglePlayPause()
                        }
                    )
                }
            }
        }
    }
}
