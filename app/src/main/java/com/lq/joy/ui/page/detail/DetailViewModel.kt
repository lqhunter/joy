package com.lq.joy.ui.page.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lq.joy.data.BaseResult
import com.lq.joy.data.sakura.ISakuraRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailViewModel(private val sakuraRepository: ISakuraRepository) : ViewModel() {

    companion object {

        fun providerFactory(sakuraRepository: ISakuraRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return DetailViewModel(sakuraRepository) as T
                }
            }
    }

    private val viewModelState = MutableStateFlow(DetailViewModelState(isLoading = true))
    val uiState = viewModelState
        .map {
            it.toUiState()
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun getDetailHtml(url: String) {
        println("detail getDetailHtml")
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val detail = sakuraRepository.getDetailData(url)
            viewModelState.update {
                when (detail) {
                    is BaseResult.Success -> {
                        it.copy(isLoading = false, detailBean = detail.data)
                    }
                    is BaseResult.Error -> {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }
}