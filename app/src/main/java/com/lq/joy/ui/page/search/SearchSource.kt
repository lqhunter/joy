package com.lq.joy.ui.page.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lq.joy.data.netfix.NaifeiService
import com.lq.joy.data.netfix.bean.NaifeiSearchItem
import com.lq.joy.data.ui.SearchBean
import retrofit2.HttpException
import java.io.IOException

class SearchSource(
    private val service: NaifeiService,
    private val searchKey: String
) : PagingSource<Int, SearchBean>() {
    override fun getRefreshKey(state: PagingState<Int, SearchBean>): Int = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchBean> {
        return try {
            val currentPage = params.key ?: 1
            val data = service.search(currentPage, params.loadSize, searchKey)

            if (data.code == 200) {
                val result = mutableListOf<SearchBean>()
                if (currentPage == 1) {
                    result.add(0, SearchBean.Title("奈飞"))
                }
                result.addAll(data.data.list.map { SearchBean.NaifeiBean(it) })

                LoadResult.Page(
                    data = result,
                    prevKey = null,
                    nextKey = if (((currentPage - 1) * params.loadSize + data.data.list.size) < data.data.total) currentPage + 1 else null
                )
            } else {
                LoadResult.Error(RuntimeException("服务器返回结果错误,code:${data.code} msg:${data.msg}"))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}