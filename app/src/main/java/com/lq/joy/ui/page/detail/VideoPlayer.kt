package com.lq.joy.ui.page.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.PlayerView
import com.lq.joy.ui.theme.Grey500
import java.io.Serializable

internal val LocalVideoPlayerController =
    compositionLocalOf<DefaultVideoController> { error("VideoPlayerController is not initialized") }

@Composable
fun rememberVideoController(
    url: String? = null
): DefaultVideoController {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

     val controller = rememberSaveable(
        context, coroutineScope,
        saver = object : Saver<DefaultVideoController, VideoPlayerState> {
            override fun restore(value: VideoPlayerState): DefaultVideoController {

                return DefaultVideoController(
                    context = context,
                    initialState = value,
                    coroutineScope = coroutineScope
                )
            }

            override fun SaverScope.save(value: DefaultVideoController): VideoPlayerState {
                return value.state.value
            }
        },
        init = {
            DefaultVideoController(
                context = context,
                initialState = VideoPlayerState(),
                coroutineScope = coroutineScope
            )
        }
    )

    url?.let { controller.setSource(it) }

    println("video $controller")
    return controller
}

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoController: DefaultVideoController,
) {
    Box(modifier = modifier) {
        AndroidView(
            factory = {
                val playerView = PlayerView(it)
                videoController.playerViewAvailable(playerView)
                playerView
            },
            modifier = Modifier.fillMaxSize(),
        )

        /*CompositionLocalProvider(LocalVideoPlayerController provides videoController) {
            VideoPlayerView(
                onSeekChange = { videoController.seekTo(it.toInt()) },
                onPlay = { if (it) videoController.play() else videoController.pause() }
            )
        }*/
    }
}


data class VideoPlayerState(
    val isPlaying: Boolean = false,
    val isReady:Boolean = false,
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
): Serializable

@Composable
private fun VideoPlayerView(
    modifier: Modifier = Modifier,
    onSeekChange: (Float) -> Unit,
    onPlay: (Boolean) -> Unit
) {
    val videoController = LocalVideoPlayerController.current
    val state by videoController.state.collectAsState()

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Grey500.copy(alpha = 0.5f))
        ) {

            IconButton(onClick = { onPlay(!state.isPlaying) }) {
                Image(
                    imageVector = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "playOrPause",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            val currentPosition = state.currentPosition.toFloat()
            var seek by remember {
                mutableStateOf(currentPosition)
            }
            Slider(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .padding(start = 8.dp, end = 16.dp),
                value = currentPosition,
                valueRange = 0f..state.duration.toFloat(),
                onValueChange = { seek = it },
                onValueChangeFinished = {
                    onSeekChange(seek)
                }
            )
        }
    }
}