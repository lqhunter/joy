package com.lq.joy.ui.page.detail

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.lq.joy.JoyApplication
import com.lq.joy.TAG
import com.lq.joy.data.Api
import com.lq.joy.data.AppRepository
import com.lq.joy.data.SourceType
import com.lq.joy.data.netfix.INaifeiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NaifeiDetailViewModel(
    private val naifeiRepository: INaifeiRepository,
    private val appRepository: AppRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        fun providerFactory(
            naifeiRepository: INaifeiRepository,
            appRepository: AppRepository,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ) = object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return NaifeiDetailViewModel(naifeiRepository, appRepository, handle) as T
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

    val videoController = DefaultVideoController(
        context = JoyApplication.context,
        initialState = VideoPlayerState(),
        coroutineScope = viewModelScope
    )

    init {
        loadDetail(vodId)
        viewModelScope.launch {
            appRepository.isFavourite(SourceType.NAIFEI, "$vodId").collect { f ->
                viewModelState.update {
                    it.copy(favourite = f)
                }
            }
        }

        viewModelScope.launch {
            videoController.state.collect { playerState ->
                Log.d(TAG, "stateUpdate进度:${playerState}")

                viewModelState.update {
                    it.copy(
                        isPlaying = playerState.isPlaying,
                        isReady = playerState.isReady,
                        lockLandscape = playerState.lockLandscape,
                        videoPositionMs = playerState.videoPositionMs
                    )
                }
            }
        }
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

    fun addFavourite() {
        viewModelState.value.favourite ?: let {
            //数据库中没有，才进行添加
            viewModelScope.launch {
                appRepository.addFavourite(
                    type = SourceType.NAIFEI,
                    uniqueTag = "$vodId",
                    jumpKey = "$vodId",
                    coverUrl = Api.NAIFEI_HOST + "/" + viewModelState.value.naifeiDetailBean!!.data.vod_pic,
                    name = viewModelState.value.naifeiDetailBean!!.data.vod_name
                )

            }
        }
    }

    fun deleteFavourite() {
        viewModelState.value.favourite?.let {
            viewModelScope.launch {
                appRepository.deletedFavourite(it)
            }
        }
    }
}