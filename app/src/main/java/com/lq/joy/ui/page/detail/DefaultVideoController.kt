package com.lq.joy.ui.page.detail

import android.content.Context
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.Listener
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import com.lq.joy.JoyApplication
import com.lq.joy.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val SinglePlayer = SimpleExoPlayer
    .Builder(JoyApplication.context)
    .build()

class DefaultVideoController(
    private val context: Context,
    private val initialState: VideoPlayerState,
    private val coroutineScope: CoroutineScope
) : IVideoController {


    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<VideoPlayerState>
        get() = _state.asStateFlow()


    private var url: String? = null
    private var playerView: PlayerView? = null


    private val exoPlayer =
        SimpleExoPlayer.Builder(context).build()
            .apply {
                Log.d(TAG, "exoPlayer init")
                playWhenReady = true
                addListener(object : Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        // media actually playing
                        Log.d(TAG, "exoPlayer onPlaybackStateChanged:${state}")
                        _state.update {
                            it.copy(isReady = state == STATE_READY)
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        Log.d(TAG, "exoPlayer isPlaying:${isPlaying}")
                        _state.update {
                            it.copy(isPlaying = isPlaying)
                        }
                    }
                })
            }

    override fun setSource(url: String) {
        Log.d(TAG, "setSource:$url")

        this.url = url
        if (playerView != null) {
            prepare()
        }
    }

    private fun prepare() {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.packageName)
        )

        if (url!!.endsWith("m3u8")) {
            val source = HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(url!!))
            exoPlayer.setMediaSource(source)
            exoPlayer.prepare()
        }

    }

    override fun play() {
        if (exoPlayer.playbackState == STATE_READY) {
            exoPlayer.play()


        }
    }

    override fun pause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.play()
        }

    }

    override fun playPauseToggle() {
//        if (videoView.isPlaying) {
//            videoView.pause()
//        } else {
//            videoView.start()
//        }
    }

    override fun quickSeekForward() {
        TODO("Not yet implemented")
    }

    override fun quickSeekRewind() {
        TODO("Not yet implemented")
    }

    override fun seekTo(position: Int) {
//        videoView.seekTo(position)
    }

    override fun reset() {

    }

    override fun release() {
        Log.d(TAG, "exoPlayer release")
        this.playerView?.player = null
        exoPlayer.release()
    }

    fun playerViewAvailable(playerView: PlayerView) {
        this.playerView = playerView
        playerView.player = exoPlayer

        if (exoPlayer.playbackState != STATE_READY) {
            if (url != null) {
                prepare()
            }
        }
    }


}