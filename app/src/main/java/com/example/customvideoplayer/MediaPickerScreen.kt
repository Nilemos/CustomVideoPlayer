package com.example.customvideoplayer

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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import kotlinx.coroutines.delay
import androidx.activity.ComponentActivity
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.view.WindowManager
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun MediaPickerScreen(
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val player = retain {
        ExoPlayer
            .Builder(context.applicationContext)
            .build()
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            player.setMediaItem(
                MediaItem.fromUri(uri)
            )
            player.prepare()
            player.play()
        }
    }

    var isPlaying by retain {
        mutableStateOf(false)
    }

    var currentPosition by retain {
        mutableLongStateOf(0L)
    }

    var duration by retain {
        mutableLongStateOf(0L)
    }

    var isSeeking by retain {
        mutableStateOf(false)
    }

    var isBuffering by retain {
        mutableStateOf(false)
    }

    var isPlayerUiVisible by retain {
        mutableStateOf(false)
    }

    var mediaItemCount by retain {
        mutableIntStateOf(0)
    }

    var isFullScreen by retain {
        mutableStateOf(false)
    }

    val activity = context as? ComponentActivity

    LaunchedEffect(isFullScreen) {
        val window = activity?.window ?: return@LaunchedEffect
        val view = window.decorView
        val controller = WindowCompat.getInsetsController(window, view)

        if (isFullScreen) {
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

    RetainedEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                super.onIsPlayingChanged(playing)
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                isBuffering = playbackState == Player.STATE_BUFFERING
                if(playbackState == Player.STATE_READY) {
                    duration = player.duration.coerceAtLeast(0)
                }
            }

            override fun onEvents(player: Player, events: Player.Events) {
                if (events.containsAny(Player.EVENT_MEDIA_ITEM_TRANSITION, Player.EVENT_TIMELINE_CHANGED)) {
                    mediaItemCount = player.mediaItemCount
                }
            }
        }
        player.addListener(listener)

        onRetire {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(isPlayerUiVisible, isSeeking, isPlaying) {
        delay(5000)
        if(!isSeeking) {
            isPlayerUiVisible = false
        }
    }

    LaunchedEffect(player, isPlaying, isSeeking) {
        while(isPlaying) {
            if(!isSeeking) {
                currentPosition = player.currentPosition.coerceAtLeast(0)
            }
            delay(16L)
        }
    }

    Column(
        modifier = modifier
            .background(Color.DarkGray)
            .fillMaxSize()
            .then(if(!isFullScreen) Modifier.statusBarsPadding() else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = if(isFullScreen) {
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
                player = player,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {
                        if (mediaItemCount == 0) {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.VideoOnly
                                )
                            )
                        } else {
                            isPlayerUiVisible = !isPlayerUiVisible
                        }
                    }
            )

            if (mediaItemCount == 0) {
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
                    visible = isPlayerUiVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    PlayerUi(
                        isPlaying = isPlaying,
                        isBuffering = isBuffering,
                        isSeeking = isSeeking,
                        currentPosition = currentPosition,
                        duration = duration,
                        isFullScreen = isFullScreen,
                        onFullScreenClick = {
                            isFullScreen = !isFullScreen
                        },
                        onSeekBarPositionChange = {
                            isSeeking = true
                            currentPosition = it
                        },
                        onSeekBarPositionChangeFinished = {
                            player.seekTo(it)
                            isSeeking = false
                        },
                        onPlayPauseClick = {
                            when {
                                !isPlaying && player.playbackState == Player.STATE_ENDED -> {
                                    player.seekTo(0)
                                    player.play()
                                }
                                !isPlaying -> player.play()
                                isPlaying -> player.pause()
                            }
                        }

                    )
                }
            }
        }
    }
}
