package com.lq.joy.ui.page.detail.naifei

import com.lq.joy.data.netfix.bean.NaifeiDetailBean
import com.lq.joy.data.sakura.bean.HomeBean
import com.lq.joy.data.sakura.bean.VideoSource
import com.lq.joy.data.ui.VideoSearchBean

data class NaifeiDetailVMState(
    val isLoading: Boolean = false,
    val currentSourceIndex: Int = 0,
    val currentEpisodeIndex: Int = -1,
    val isPlaying: Boolean = false,
    val isFavorite: Boolean = false,
    val isDetailExpended: Boolean = false,
    val videoSearchBean: VideoSearchBean.NaifeiBean,
    val detailBean: NaifeiDetailBean? = null
) {
/*    fun toUIState(): NaifeiDetailUiState {
        return if (detailBean == null) {
            NaifeiDetailUiState.NoData(isLoading = false)
        } else {
            NaifeiDetailUiState.HasData(
                isLoading = false,
                isPlaying = isPlaying,
                isFavorite = isFavorite,
                isDetailExpended = isDetailExpended,
            )
        }

    }*/
}

sealed interface NaifeiDetailUiState {
    val isLoading: Boolean

    data class NoData(
        override val isLoading: Boolean
    ) : NaifeiDetailUiState

    data class HasData(
        override val isLoading: Boolean,
        val isPlaying: Boolean,
        val isFavorite: Boolean,
        val isDetailExpended: Boolean,
        val videoSource: VideoSource,
        val currentEpisodeIndex: Int
    ) : NaifeiDetailUiState
}