package com.lq.joy.ui.page.detail

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.lq.joy.TAG
import com.lq.joy.data.Api
import com.lq.joy.data.AppRepository
import com.lq.joy.data.BaseResult
import com.lq.joy.data.SourceType
import com.lq.joy.data.sakura.ISakuraRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SakuraDetailViewModel(
    private val sakuraRepository: ISakuraRepository,
    private val appRepository: AppRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val episodeUrl: String = Uri.decode(savedStateHandle.get<String>("episodeUri")!!)

    companion object {

        fun providerFactory(
            sakuraRepository: ISakuraRepository,
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
                return SakuraDetailViewModel(sakuraRepository, appRepository, handle) as T
            }
        }
    }

    private val viewModelState =
        MutableStateFlow(DetailVMState(isLoading = false, sourceType = SourceType.SAKURA))
    val uiState = viewModelState
        .map {
            it.toUIState()
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUIState()
        )

    init {
        getDetailHtml(Api.HOME + episodeUrl)

        viewModelScope.launch {
            appRepository.isFavourite(SourceType.SAKURA, episodeUrl).collect { f ->
                viewModelState.update {
                    it.copy(favourite = f)
                }
            }
        }

    }

    private fun getDetailHtml(url: String) {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val detail = sakuraRepository.getDetailData(url)
            viewModelState.update {
                when (detail) {
                    is BaseResult.Success -> {
                        it.copy(isLoading = false, sakuraDetailBean = detail.data)
                    }
                    is BaseResult.Error -> {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }


    suspend fun getPlayUrl(htmlUrl: String): String? {
        return withContext(Dispatchers.IO) {
            when (val result = sakuraRepository.getPlayUrl(Api.HOME + htmlUrl)) {
                is BaseResult.Success -> {
                    Log.d(TAG, "BaseResult.Success")
                    result.data
                }
                is BaseResult.Error -> {
                    Log.d(TAG, "BaseResult.Error")
                    null
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

    fun addFavourite() {
        viewModelState.value.favourite ?: let {
            //数据库中没有，才进行添加
            viewModelScope.launch {
                appRepository.addFavourite(
                    type = SourceType.SAKURA,
                    uniqueTag = episodeUrl,
                    jumpKey = episodeUrl,
                    coverUrl = viewModelState.value.sakuraDetailBean!!.coverUrl,
                    name = viewModelState.value.sakuraDetailBean!!.animationName
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