package com.lq.joy.ui.page.detail

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.lq.joy.data.SourceType
import com.lq.joy.data.netfix.INaifeiRepository
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
        MutableStateFlow(DetailVMState(isLoading = false, sourceType = SourceType.NAIFEI))

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

    fun selectEpisode(index: Int) {
        viewModelState.update {
            it.copy(
                currentEpisodeIndex = index,
            )
        }
    }

    fun selectSource(index: Int) {
        viewModelState.update {
            it.copy(
                currentSourceIndex = index,
                currentEpisodeIndex = -1,
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
                    it.copy(isLoading = false, naifeiDetailBean = bean)
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