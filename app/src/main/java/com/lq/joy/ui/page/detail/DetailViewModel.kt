package com.lq.joy.ui.page.detail

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.lq.joy.TAG
import com.lq.joy.data.Api
import com.lq.joy.data.BaseResult
import com.lq.joy.data.sakura.ISakuraRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailViewModel(
    private val sakuraRepository: ISakuraRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val episodeUrl: String = Uri.decode(savedStateHandle.get<String>("episodeUri")!!)

    companion object {

        fun providerFactory(
            sakuraRepository: ISakuraRepository,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ) = object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return DetailViewModel(sakuraRepository, handle) as T
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

    init {
        getDetailHtml(Api.HOME + episodeUrl)
    }

    private fun getDetailHtml(url: String) {
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

    fun getUrlAndPlay(index: Int) {
        Log.d(TAG, "video getUrlAndPlay:${index}")

        viewModelState.update { it.copy(isLoading = true, currentIndex = index) }

        viewModelScope.launch {
            viewModelState.value.detailBean?.episodes?.get(index)?.let { playBean ->
                playBean.playUrl?.let {
                    //
                    Log.d(TAG, "video 缓存中存在")

                } ?: let {
                    playBean.playHtmlUrl?.also {
                        val result = sakuraRepository.getPlayUrl(Api.HOME + it)
                        viewModelState.update {
                            when (result) {
                                is BaseResult.Success -> {
                                    Log.d(TAG, "BaseResult.Success")
                                    it.updateUrl(index, result.data).copy(isLoading = false)
                                }
                                is BaseResult.Error -> {
                                    Log.d(TAG, "BaseResult.Error")
                                    it.copy(isLoading = false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}