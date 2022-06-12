package com.lq.joy.data.netfix

import androidx.paging.PagingData
import com.lq.joy.data.ui.VideoSearchBean
import kotlinx.coroutines.flow.Flow

interface INaifeiRepository {

    fun search(
        limit: Int,
        wd: String
    ): Flow<PagingData<VideoSearchBean>>
}