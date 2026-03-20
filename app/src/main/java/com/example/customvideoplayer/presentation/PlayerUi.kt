@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.customvideoplayer.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.customvideoplayer.R
import java.util.Locale

@Composable
fun PlayerUi(
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    isSeeking: Boolean,
    isBuffering: Boolean,
    onSeekBarPositionChange: (Long) -> Unit,
    onSeekBarPositionChangeFinished: (Long) -> Unit,
    onPlayPauseClick: () -> Unit,
    isFullScreen: Boolean,
    onFullScreenClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ){

        if(isBuffering){
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(20.dp)
            )
        } else {
            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(100.dp)
            ) {
                Icon(
                    imageVector = if(isPlaying) {
                        ImageVector.vectorResource(R.drawable.pause_24px)
                    } else {
                        ImageVector.vectorResource(R.drawable.baseline_play_arrow_24)
                    },
                    contentDescription = if(isPlaying) "Pause" else "Play",
                    modifier = Modifier
                        .size(50.dp),
                    tint = Color.White
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = formatDuration(currentPosition),
                color = Color.White
            )

            Slider(
                value = currentPosition.toFloat(),
                onValueChange = { newPosition ->
                    onSeekBarPositionChange(newPosition.toLong())
                },
                onValueChangeFinished = {
                    onSeekBarPositionChangeFinished(currentPosition)
                },
                valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                modifier = Modifier
                    .weight(1f),
                thumb = {
                    Box (
                        modifier = Modifier
                            .size(15.dp)
                            .shadow(elevation = 4.dp, CircleShape)
                            .background(Color.White)
                    )
                },
                track = { sliderState ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        val progress = if (duration > 0) sliderState.value / duration else 0f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            )

            Text(
                text = formatDuration(duration),
                color = Color.White
            )

            IconButton(
                onClick = onFullScreenClick,
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = if(!isFullScreen) {
                        ImageVector.vectorResource(R.drawable.fullscreen_24px)
                    } else {
                        ImageVector.vectorResource(R.drawable.fullscreen_exit_24px)

                    },
                    contentDescription = if(!isFullScreen) "Fullscreen" else "Exit fullscreen",
                    tint = Color.White
                )
            }
        }
    }
}

fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0){
        String.format(Locale.US, format = "%d:%02d:%02d", hours, minutes, seconds)
    } else {

        String.format(Locale.US, format = "%02d:%02d", minutes, seconds)
    }
}
