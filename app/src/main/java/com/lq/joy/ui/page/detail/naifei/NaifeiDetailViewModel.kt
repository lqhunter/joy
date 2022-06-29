package com.lq.joy.ui.page.detail.naifei

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.lq.joy.data.netfix.INaifeiRepository
import com.lq.joy.data.ui.VideoSearchBean
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NaifeiDetailViewModel(
    private val naifeiRepository: INaifeiRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        fun providerFactory(
            naifeiRepository: INaifeiRepository,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ) = object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return NaifeiDetailViewModel(naifeiRepository, handle) as T
            }
        }
    }

    private val vodId = savedStateHandle.get<Int>("vod_id")!!

    private val viewModelState =
        MutableStateFlow(NaifeiDetailVMState(isLoading = false))

    val uiState = viewModelState
        .map {
            it.toUIState()
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUIState()
        )

    init {
        loadDetail(vodId)
    }

    fun selectIndex(index: Int) {
        viewModelState.update {
            it.copy(
                currentEpisodeIndex = index,
            )
        }
    }

    private fun loadDetail(vodId: Int) {
        viewModelState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            try {
                val bean = naifeiRepository.detail(vodId)
                viewModelState.update {
                    it.copy(isLoading = false, detailBean = bean)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewModelState.update {
                    it.copy(isLoading = false)
                }
            }

        }
    }
}