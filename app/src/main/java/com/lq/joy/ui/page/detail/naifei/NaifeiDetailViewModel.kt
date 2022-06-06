package com.lq.joy.ui.page.detail.naifei

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.lq.joy.data.netfix.INaifeiRepository
import com.lq.joy.data.ui.SearchBean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

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

    private val searchBean = savedStateHandle.get<SearchBean>("search")!! as SearchBean.NaifeiBean


    private val viewModelState = MutableStateFlow(NaifeiDetailVMState(searchBean = searchBean))
    val uiState = viewModelState


    fun selectIndex(index:Int) {
        viewModelState.update { it.copy(currentIndex = index) }
    }
}