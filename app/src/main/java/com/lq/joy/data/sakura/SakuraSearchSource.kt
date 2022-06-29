package com.lq.joy.data.sakura

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lq.joy.data.Api
import com.lq.joy.data.ui.VideoSearchBean

class SakuraSearchSource(private val searchKey: String) : PagingSource<Int, VideoSearchBean>() {
    override fun getRefreshKey(state: PagingState<Int, VideoSearchBean>): Int = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoSearchBean> {
        val currentPage = params.key ?: 1

        val data = SakuraService.getSearchData(Api.SEARCH.plus(searchKey).plus("/?page=${currentPage}"))

        if(data != null) {
            val result = mutableListOf<VideoSearchBean>()
            result.addAll(data.data.map { origin ->
                VideoSearchBean.SakuraBean(
                    id = origin.id,
                    name = origin.name,
                    coverUrl = origin.coverUrl,
                    newestEpisode = origin.newestEpisode,
                    newestEpisodeUrl = origin.newestEpisodeUrl,
                    detailUrl = origin.detailUrl,
                    tags = origin.tags
                )
            })
            return LoadResult.Page(
                data = result,
                prevKey = null,
                nextKey = if (data.totalPage != 0) currentPage + 1 else null //最后一页没有 "lastn"字段
            )
        } else {
            return LoadResult.Error(RuntimeException(""))
        }
    }
}