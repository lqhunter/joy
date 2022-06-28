package com.lq.joy.ui.page.detail

import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.flow.StateFlow

interface IVideoController {

    fun setSource(url: String)

    fun setItems(mediaItems: List<MediaItem>, windowIndex: Int)

    fun getItemsCount():Int

    fun play()

    fun pause()

    fun playPauseToggle()

    fun quickSeekForward()

    fun quickSeekRewind()

    fun seekTo(windowIndex: Int, positionMs: Long = 0)

    fun reset()

    val state: StateFlow<VideoPlayerState>

    fun release()
}