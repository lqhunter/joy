package com.lq.joy.ui.page.detail.naifei

import com.lq.joy.data.Api.NAIFEI_HOST
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
    val detailBean: NaifeiDetailBean? = null
) {
    fun toUIState(): NaifeiDetailUiState {
        return if (detailBean == null) {
            NaifeiDetailUiState.NoData(isLoading = isLoading)
        } else {
            NaifeiDetailUiState.HasData(
                isLoading = isLoading,
                isPlaying = isPlaying,
                isFavorite = isFavorite,
                videoSource = detailBean.data.vod_play_list[currentSourceIndex],
                currentEpisodeIndex = currentEpisodeIndex,
                coverUrl = NAIFEI_HOST + "/" + detailBean.data.vod_pic,
                name = detailBean.data.vod_name,
                score = detailBean.data.vod_score
            )
        }

    }
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
        val videoSource: NaifeiDetailBean.Data.VodPlay,
        val currentEpisodeIndex: Int,
        val coverUrl: String,
        val name:String,
        var score:String,
    ) : NaifeiDetailUiState
}