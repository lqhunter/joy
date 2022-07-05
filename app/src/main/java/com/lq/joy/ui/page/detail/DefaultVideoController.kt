package com.lq.joy.ui.page.detail

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.compose.runtime.collectAsState
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
import com.lq.joy.R
import com.lq.joy.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
    private var ivScreenLock: ImageView? = null

    init {
        coroutineScope.launch {
            state.stateIn(coroutineScope).collect { s ->
                ivScreenLock?.run {
                    if (s.lockLandscape) {
                        setImageResource(R.drawable.outline_lock_white_48)
                    } else {
                        setImageResource(R.drawable.outline_lock_open_white_48)
                    }
                }

            }
        }

    }


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

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        val index = mediaItem?.playbackProperties?.tag
                        if (index is Int) {
                            _state.update {
                                it.copy(episodeIndex = index)
                            }
                        }
                    }
                })
            }

    override fun setSource(url: String) {

    }

    override fun setItems(mediaItems: List<MediaItem>, windowIndex: Int) {
        reset()
        exoPlayer.setMediaItems(mediaItems, windowIndex, 0)
        exoPlayer.prepare()
    }

    override fun getItemsCount(): Int {
        return exoPlayer.mediaItemCount
    }


    override fun play() {
        if (exoPlayer.playbackState == STATE_READY) {
            exoPlayer.play()
        }
    }

    override fun pause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        }
    }

    override fun playPauseToggle() {

    }

    override fun quickSeekForward() {
        TODO("Not yet implemented")
    }

    override fun quickSeekRewind() {
        TODO("Not yet implemented")
    }

    override fun seekTo(windowIndex: Int, positionMs: Long) {
        exoPlayer.seekTo(windowIndex, positionMs)
    }

    override fun reset() {
        exoPlayer.stop()
        _state.update {
            it.copy(isPlaying = false, isReady = false)
        }
    }

    override fun release() {
        Log.d(TAG, "exoPlayer release")
        this.playerView?.player = null
        exoPlayer.release()
    }

    fun playerViewAvailable(playerView: PlayerView) {
        this.playerView = playerView
        ivScreenLock = playerView.findViewById(R.id.exo_lock)
        ivScreenLock?.run {
            if (_state.value.lockLandscape) {
                setImageResource(R.drawable.outline_lock_white_48)
            } else {
                setImageResource(R.drawable.outline_lock_open_white_48)
            }

            setOnClickListener {
                _state.update {
                    it.copy(lockLandscape = !it.lockLandscape)
                }
            }

        }

        playerView.player = exoPlayer
    }

    override fun setLockShow(show: Boolean) {
        ivScreenLock?.run {
            visibility = if (show) View.VISIBLE else View.GONE
        }
    }


}