package com.lq.joy.ui.page.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lq.joy.data.netfix.INaifeiRepository
import com.lq.joy.data.netfix.bean.NaifeiSearchItem
import com.lq.joy.data.sakura.ISakuraRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(
    private val sakuraRepository: ISakuraRepository,
    private val naifeiRepository: INaifeiRepository
) : ViewModel() {


    private val viewModelState = MutableStateFlow(SearchViewModelState())

    val uiState: StateFlow<SearchViewModelState> = viewModelState

    var searchFlow: Flow<PagingData<NaifeiSearchItem>>? = null


    fun search(key: String) {
        viewModelScope.launch {
            searchFlow = naifeiRepository.search(10, key).cachedIn(viewModelScope)
            viewModelState.update { it.copy(isSearching = true, key = key) }
        }

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