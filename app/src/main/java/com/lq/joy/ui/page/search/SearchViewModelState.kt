package com.lq.joy.ui.page.search

import androidx.paging.PagingData
import com.lq.joy.data.ui.VideoSearchBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class SearchViewModelState(
    val reSearch: Boolean = false,
    val key: String = "",
    val naifeiFlow: Flow<PagingData<VideoSearchBean>> = emptyFlow(),
    val sakuraFlow: Flow<PagingData<VideoSearchBean>> = emptyFlow()
)



