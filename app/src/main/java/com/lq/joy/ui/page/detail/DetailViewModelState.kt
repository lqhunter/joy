package com.lq.joy.ui.page.detail

import com.lq.joy.data.sakura.bean.DetailBean

data class DetailViewModelState(
    val isLoading: Boolean = false,
    val detailBean: DetailBean? = null,
) {
    fun toUiState(): DetailUiState {
        return if (detailBean == null) {
            DetailUiState.NoData(isLoading)
        } else {
            DetailUiState.HasData(isLoading, data = detailBean)
        }
    }
}

sealed interface DetailUiState {
    val isLoading: Boolean

    data class NoData(
        override val isLoading: Boolean
    ) : DetailUiState

    data class HasData(
        override val isLoading: Boolean,
        val data: DetailBean
    ) : DetailUiState


}