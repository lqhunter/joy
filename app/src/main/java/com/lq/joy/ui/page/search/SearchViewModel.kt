package com.lq.joy.ui.page.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lq.joy.TAG
import com.lq.joy.data.AppRepository
import com.lq.joy.data.netfix.INaifeiRepository
import com.lq.joy.data.sakura.ISakuraRepository
import com.lq.joy.data.ui.VideoSearchBean
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(
    private val sakuraRepository: ISakuraRepository,
    private val naifeiRepository: INaifeiRepository,
    private val appRepository: AppRepository
) : ViewModel() {


    private val viewModelState = MutableStateFlow(SearchViewModelState())

    val uiState: StateFlow<SearchViewModelState> = viewModelState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        viewModelState.value
    )


    init {
        viewModelScope.launch {
            appRepository.getSearchFilter().collect { s ->
                s?.let {
                    viewModelState.update {
                        it.copy(
                            filter = s
                        )
                    }
                }
            }
        }

    }

    fun search(key: String) {
        viewModelState.update {
            it.copy(
                naifeiFlow = naifeiRepository.search(10, key).cachedIn(viewModelScope),
                sakuraFlow = sakuraRepository.search(key).cachedIn(viewModelScope)
            )
        }
    }


    companion object {
        fun providerFactory(
            sakuraRepository: ISakuraRepository,
            naifeiRepository: INaifeiRepository,
            appRepository: AppRepository
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel(sakuraRepository, naifeiRepository, appRepository) as T
            }
        }
    }

}