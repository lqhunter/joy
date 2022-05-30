package com.lq.joy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lq.joy.data.BaseResult
import com.lq.joy.data.sakura.ISakuraRepository
import com.lq.lib_sakura.bean.HomeBean
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val sakuraRepository: ISakuraRepository) : ViewModel() {

    companion object {

        fun providerFactory(sakuraRepository: ISakuraRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(modelClass: Class<T>):T {
                    return HomeViewModel(sakuraRepository) as T

                }

            }
    }

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))
    val uiState = viewModelState
        .map {
            it.toUiState()
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        getHomeHtml()
    }

    fun getHomeHtml() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val bean = sakuraRepository.getHomeData()
            viewModelState.update {
                when (bean) {
                    is BaseResult.Success -> {
                        it.copy(isLoading = false, data = bean.data)
                    }
                    is BaseResult.Error -> {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

}



