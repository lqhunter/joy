package com.lq.joy.ui.page.favourite

import android.os.Bundle
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.savedstate.SavedStateRegistryOwner
import com.lq.joy.data.AppRepository
import com.lq.joy.db.Favourite
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavouriteViewModel(
    private val appRepository: AppRepository,
) : ViewModel() {

    companion object {
        fun providerFactory(
            appRepository: AppRepository,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FavouriteViewModel(appRepository) as T
            }

        }
    }

    private val viewModelState = MutableStateFlow(
        FavouriteUiState(
            pagingData = appRepository.getFavouriteList()
        )
    )

    val uiState: StateFlow<FavouriteUiState> = viewModelState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        viewModelState.value
    )

    fun deleteFavourite(favourite: Favourite) {
        viewModelScope.launch {
            appRepository.deletedFavourite(favourite)
        }
    }

}

data class FavouriteUiState(
    val pagingData: Flow<PagingData<Favourite>>
)