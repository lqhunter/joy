package com.lq.joy.ui.page.detail

import kotlinx.coroutines.flow.StateFlow

interface VideoPlayerController {

    fun setSource(url: String)

    fun play()

    fun pause()

    fun playPauseToggle()

    fun quickSeekForward()

    fun quickSeekRewind()

    fun seekTo(position: Long)

    fun reset()

    val state: StateFlow<VideoPlayerState>
}