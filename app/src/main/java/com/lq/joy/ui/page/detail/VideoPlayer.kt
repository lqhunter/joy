package com.lq.joy.ui.page.detail

import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.ui.PlayerView
import com.lq.joy.R
import com.lq.joy.TAG
import java.io.Serializable


@Composable
fun rememberVideoController(
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
    return controller
}

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoController: DefaultVideoController,
) {
    Icons.Rounded.Lock
    Box(modifier = modifier) {
        AndroidView(
            factory = {
                val playerView = PlayerView(it)
                videoController.playerViewAvailable(playerView)
                playerView
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}


data class VideoPlayerState(
    val episodeIndex: Int = -1,
    val isPlaying: Boolean = false,
    val isReady: Boolean = false,
    val controlsVisible: Boolean = true,
    val controlsEnabled: Boolean = true,
    val gesturesEnabled: Boolean = true,
    val lockLandscape:Boolean = false,
    val duration: Long = 1L,
    val currentPosition: Long = 1L,
    val secondaryProgress: Long = 1L,
    val videoSize: Pair<Float, Float> = 1920f to 1080f,
/*    val draggingProgress: DraggingProgress? = null,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val quickSeekAction: QuickSeekAction = QuickSeekAction.none()*/
) : Serializable

