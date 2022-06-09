package com.lq.joy.data.netfix

import androidx.paging.*
import com.google.gson.Gson
import com.lq.joy.JoyApplication
import com.lq.joy.data.netfix.bean.NaifeiSearchBean
import com.lq.joy.data.sakura.bean.PlayBean
import com.lq.joy.data.ui.SearchBean
import com.lq.joy.utils.getLocalString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

class FakeNaifeiRepository : INaifeiRepository {
    override fun search(limit: Int, wd: String): Flow<PagingData<SearchBean>> {
        return Pager(PagingConfig(limit)) {
            FakeSource()
        }.flow
    }
}

private class FakeSource : PagingSource<Int, SearchBean>() {
    override fun getRefreshKey(state: PagingState<Int, SearchBean>): Int = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchBean> {
        return try {
            val currentPage = params.key ?: 1
            delay(3000)
            val data = fakeData()

            if (data?.code == 200) {
                val result = mutableListOf<SearchBean>()
                if (currentPage == 1) {
                    result.add(0, SearchBean.Title("奈飞"))
                }
                result.addAll(data.data.list.map { origin ->
                    SearchBean.NaifeiBean(
                        origin.vod_name,
                        origin.vod_pic,
                        origin.vod_area,
                        origin.vod_class,
                        origin.vod_remarks,
                        origin.vod_douban_score,
                        origin.run {
                            val r = mutableListOf<PlayBean>()
                            vod_play_url.split("#").forEach {
                                val split = it.split("$")
                                if (split.size == 2) {
                                    r.add(PlayBean(split[0], split[1]))
                                }
                            }
                            r
                        }
                    )
                })

                LoadResult.Page(
                    data = result,
                    prevKey = null,
                    nextKey = if (((currentPage - 1) * params.loadSize + data.data.list.size) < data.data.total) currentPage + 1 else null
                )
            } else {
                LoadResult.Error(RuntimeException("服务器返回结果错误,code:${data?.code} msg:${data?.msg}"))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    private fun fakeData(): NaifeiSearchBean? {
        val json = getLocalString(JoyApplication.context, "search.json")
        val gson = Gson()
        return gson.fromJson(json, NaifeiSearchBean::class.java)
    }
}