package com.lq.joy.ui.page.detail.sakura

import com.lq.joy.data.sakura.bean.DetailBean

data class SakuraDetailVMState(
    val isLoading: Boolean = false,
    val detailBean: DetailBean? = null,
    val currentIndex: Int = -1,
    val isPlaying: Boolean = false
) {
    fun toUiState(): DetailUiState {
        return if (detailBean == null) {
            DetailUiState.SakuraNoData(isLoading, isPlaying = isPlaying)
        } else {
            DetailUiState.SakuraHasData(
                isLoading,
                data = detailBean,
                currentIndex = currentIndex,
                isPlaying = isPlaying,
                isFavorite = true,
                isDetailExpended = false
            )
        }
    }

    fun updateUrl(index: Int, playUrl: String): SakuraDetailVMState {
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

    data class SakuraNoData(
        override val isLoading: Boolean,
        override val isPlaying: Boolean
    ) : DetailUiState

    data class SakuraHasData(
        override val isLoading: Boolean,
        override val isPlaying: Boolean,
        val data: DetailBean,
        val currentIndex: Int,
        val isFavorite: Boolean,
        val isDetailExpended: Boolean
    ) : DetailUiState {

        fun getCurrentPlayUrl(): String? {
            return if (currentIndex == -1)
                null
            else
                data.episodes[currentIndex].playUrl
        }
    }


}