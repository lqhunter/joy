package com.lq.joy.ui.home

import com.lq.lib_sakura.bean.HomeBean

data class HomeViewModelState(
    val isLoading: Boolean = false,
    val selectedId: String? = null,
    val data: HomeBean? = null
) {
    fun toUiState(): HomeUiState {
        return if (data == null) {
            HomeUiState.NoData(isLoading = isLoading)
        } else {
            HomeUiState.HasData(
                isLoading = isLoading,
                selectedId = selectedId,
                data = data
            )
        }
    }
}

sealed interface HomeUiState {
    val isLoading: Boolean

    data class NoData(
        override val isLoading: Boolean
    ) : HomeUiState

    data class HasData(
        override val isLoading: Boolean,
        val selectedId: String?,
        val data: HomeBean
    ) : HomeUiState
}

