package com.lq.joy.ui.page.detail

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.PlayerView.SHOW_BUFFERING_WHEN_PLAYING
import com.google.android.exoplayer2.util.Log
import com.lq.joy.R
import com.lq.joy.TAG
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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
                        //??????????????????tag,???????????????
                        if (index is Int) {
                            _state.update {
                                it.copy(episodeIndex = index)
                            }
                        }
                    }

                    override fun onPlayerError(error: ExoPlaybackException) {
                        Toast.makeText(context, "????????????", Toast.LENGTH_LONG).show()
                    }

                    override fun onEvents(player: Player, events: Events) {
                        if (events.containsAny(
                                EVENT_POSITION_DISCONTINUITY,
                                EVENT_TIMELINE_CHANGED
                            )
                        ) {

                        }
                        if (events.containsAny(
                                EVENT_PLAYBACK_STATE_CHANGED,
                                EVENT_PLAY_WHEN_READY_CHANGED,
                                EVENT_IS_PLAYING_CHANGED
                            )
                        ) {

                        }
                    }

                })
            }


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

        coroutineScope.launch {
            while (true) {
                delay(1000L)
                if (_state.value.isPlaying) {
                    _state.update {
                        Log.d(TAG, "??????:${exoPlayer.currentPosition} | ${this@DefaultVideoController}")
                        it.copy(videoPositionMs = exoPlayer.currentPosition)
                    }
                }
            }
        }
    }

    override fun setSource(url: String) {

    }

    override fun setItems(mediaItems: List<MediaItem>, windowIndex: Int, start: Long) {
        reset()
        exoPlayer.setMediaItems(mediaItems, windowIndex, start)
        exoPlayer.prepare()
    }


    override fun getItemsCount(): Int {
        return exoPlayer.mediaItemCount
    }


    override fun play() {
        Log.d(TAG, "play()")
        if (exoPlayer.playbackState == STATE_READY) {
            exoPlayer.play()
        }
    }

    override fun pause() {
        Log.d(TAG, "pause()")
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

        playerView.setShowBuffering(SHOW_BUFFERING_WHEN_PLAYING)
        playerView.player = exoPlayer
    }

    override fun setLockShow(show: Boolean) {
        ivScreenLock?.run {
            visibility = if (show) View.VISIBLE else View.GONE
        }
    }


}