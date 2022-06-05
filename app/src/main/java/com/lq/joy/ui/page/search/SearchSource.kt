package com.lq.joy.ui.page.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lq.joy.data.netfix.NaifeiService
import com.lq.joy.data.netfix.bean.NaifeiSearchItem
import retrofit2.HttpException
import java.io.IOException

class SearchSource(
    private val service: NaifeiService,
    private val searchKey: String
) : PagingSource<Int, NaifeiSearchItem>() {
    override fun getRefreshKey(state: PagingState<Int, NaifeiSearchItem>): Int = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NaifeiSearchItem> {
        return try {
            val currentPage = params.key ?: 1
            val result = service.search(currentPage, params.loadSize, searchKey)

            if (result.code == 200) {
                val list = result.data.list
                LoadResult.Page(
                    data = list,
                    prevKey = null,
                    nextKey = if (((currentPage - 1) * params.loadSize + list.size) < result.data.total) currentPage + 1 else null
                )
            } else {
                LoadResult.Error(RuntimeException("服务器返回结果错误,code:${result.code} msg:${result.msg}"))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}