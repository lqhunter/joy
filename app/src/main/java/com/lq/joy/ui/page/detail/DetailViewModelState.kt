package com.lq.joy.ui.page.detail

import com.lq.joy.data.sakura.bean.DetailBean

data class DetailViewModelState(
    val isLoading: Boolean = false,
    val detailBean: DetailBean? = null,
    val currentIndex: Int = 0,
    val isPlaying: Boolean = false
//    val isVideoLoading:Boolean = false
) {
    fun toUiState(): DetailUiState {
        return if (detailBean == null) {
            DetailUiState.NoData(isLoading, isPlaying = isPlaying)
        } else {
            DetailUiState.HasData(
                isLoading,
                data = detailBean,
                currentIndex = currentIndex,
                isPlaying = isPlaying
//                isVideoLoading = isVideoLoading
            )
        }
    }

    fun updateUrl(index: Int, playUrl: String): DetailViewModelState {
        return copy(detailBean = detailBean?.episodes?.let {
            val old = it[index]
            val new = old.copy(playUrl = playUrl)

            val changeList = it.toMutableList()
            changeList.remove(changeList[index])
            changeList.add(index, new)
            return@let detailBean.copy(episodes = changeList)
        })
    }
}

sealed interface DetailUiState {
    val isLoading: Boolean
    val isPlaying: Boolean

    data class NoData(
        override val isLoading: Boolean,
        override val isPlaying: Boolean
    ) : DetailUiState

    data class HasData(
        override val isLoading: Boolean,
        val data: DetailBean,
        val currentIndex: Int,
        override val isPlaying: Boolean,
//        val isVideoLoading:Boolean
    ) : DetailUiState {

        fun getCurrentPlayUrl(): String? {
            return data.episodes[currentIndex].playUrl
        }
    }


}