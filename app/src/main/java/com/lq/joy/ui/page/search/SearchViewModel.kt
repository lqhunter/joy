package com.lq.joy.ui.page.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lq.joy.TAG
import com.lq.joy.data.netfix.INaifeiRepository
import com.lq.joy.data.sakura.ISakuraRepository
import com.lq.joy.data.ui.VideoSearchBean
import kotlinx.coroutines.flow.*

class SearchViewModel(
    private val sakuraRepository: ISakuraRepository,
    private val naifeiRepository: INaifeiRepository
) : ViewModel() {


    private val viewModelState = MutableStateFlow(SearchViewModelState())

    val uiState: StateFlow<SearchViewModelState> = viewModelState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        viewModelState.value
    )

    var naifeiSearchFlow: Flow<PagingData<VideoSearchBean>>? = null
    var sakuraSearchFlow: Flow<PagingData<VideoSearchBean>>? = null


    fun search(key: String) {
        Log.d(TAG, "search:${key}")
        viewModelState.update { it.copy(reSearch = true, key = key) }
        naifeiSearchFlow = naifeiRepository.search(10, key).cachedIn(viewModelScope)
        sakuraSearchFlow = sakuraRepository.search("").cachedIn(viewModelScope)
    }

    companion object {
        fun providerFactory(
            sakuraRepository: ISakuraRepository,
            naifeiRepository: INaifeiRepository
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel(sakuraRepository, naifeiRepository) as T
            }
        }
    }

}