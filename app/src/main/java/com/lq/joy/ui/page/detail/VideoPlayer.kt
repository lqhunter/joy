package com.lq.joy.ui.page.detail

import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.lq.joy.ui.theme.Grey500

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoPlayerState: VideoPlayerState,
    onSeekChange: (Float) -> Unit,
    onPlayOrPause: (Boolean) -> Unit
) {
    Box(modifier = modifier) {
        val context = LocalContext.current
        val videoView by rememberSaveable { mutableStateOf(VideoView(context)) }

        AndroidView(
            factory = {
                videoView
            },
            modifier = Modifier.fillMaxSize()
        )

        VideoPlayerView(
            videoPlayerState = videoPlayerState,
            onSeekChange = onSeekChange,
            onPlay = onPlayOrPause
        )
    }
}


data class VideoPlayerState(
    val isPlaying: Boolean = true,
    val controlsVisible: Boolean = true,
    val controlsEnabled: Boolean = true,
    val gesturesEnabled: Boolean = true,
    val duration: Long = 1L,
    val currentPosition: Long = 1L,
    val secondaryProgress: Long = 1L,
    val videoSize: Pair<Float, Float> = 1920f to 1080f,
/*    val draggingProgress: DraggingProgress? = null,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val quickSeekAction: QuickSeekAction = QuickSeekAction.none()*/
)

@Composable
private fun VideoPlayerView(
    videoPlayerState: VideoPlayerState,
    modifier: Modifier = Modifier,
    onSeekChange: (Float) -> Unit,
    onPlay: (Boolean) -> Unit
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Grey500.copy(alpha = 0.5f))
        ) {

            IconButton(onClick = { onPlay(!videoPlayerState.isPlaying) }) {
                Image(
                    imageVector = if (videoPlayerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "playOrPause",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            val currentPosition = videoPlayerState.currentPosition.toFloat()
            var seek by remember {
                mutableStateOf(currentPosition)
            }
            Slider(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .padding(start = 8.dp, end = 16.dp),
                value = currentPosition,
                valueRange = 0f..videoPlayerState.duration.toFloat(),
                onValueChange = { seek = it },
                onValueChangeFinished = {
                    onSeekChange(seek)
                }
            )
        }
    }
}