package com.lq.joy.ui.page.detail.naifei

import com.lq.joy.data.ui.VideoSearchBean

data class NaifeiDetailVMState(
    val currentIndex: Int = -1,
    val isPlaying: Boolean = false,
    val isFavorite: Boolean = false,
    val isDetailExpended: Boolean = false,
    val videoSearchBean: VideoSearchBean.NaifeiBean
) {

}
