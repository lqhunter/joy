package com.lq.joy.ui.page.detail.naifei

import com.lq.joy.data.ui.VideoSearchBean

data class NaifeiDetailVMState(
    val currentSourceIndex:Int = -1,
    val currentEpisodeIndex: Int = -1,
    val isPlaying: Boolean = false,
    val isFavorite: Boolean = false,
    val isDetailExpended: Boolean = false,
    val videoSearchBean: VideoSearchBean.NaifeiBean
) {

}
