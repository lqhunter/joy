package com.lq.joy.ui.page.detail

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultVideoPlayerController(
    private val context: Context,
    private val initialState: VideoPlayerState,
) : VideoPlayerController {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<VideoPlayerState>
        get() = _state.asStateFlow()

    override fun setSource(url: String) {
        TODO("Not yet implemented")
    }

    override fun play() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun playPauseToggle() {
        TODO("Not yet implemented")
    }

    override fun quickSeekForward() {
        TODO("Not yet implemented")
    }

    override fun quickSeekRewind() {
        TODO("Not yet implemented")
    }

    override fun seekTo(position: Long) {
        TODO("Not yet implemented")
    }

    override fun reset() {
        TODO("Not yet implemented")
    }


}