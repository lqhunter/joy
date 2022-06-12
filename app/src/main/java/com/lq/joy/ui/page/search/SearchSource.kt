package com.lq.joy.ui.page.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.lq.joy.JoyApplication
import com.lq.joy.data.netfix.NaifeiService
import com.lq.joy.data.netfix.bean.NaifeiSearchBean
import com.lq.joy.data.sakura.bean.PlayBean
import com.lq.joy.data.ui.VideoSearchBean
import com.lq.joy.utils.getLocalString
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

class SearchSource(
    private val service: NaifeiService,
    private val searchKey: String
) : PagingSource<Int, VideoSearchBean>() {
    override fun getRefreshKey(state: PagingState<Int, VideoSearchBean>): Int = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoSearchBean> {
        return try {
            val currentPage = params.key ?: 1
//            val data = service.search(currentPage, params.loadSize, searchKey)
            delay(3000)
            val data = fakeData()

            if (data?.code == 200) {
                val result = mutableListOf<VideoSearchBean>()
                result.addAll(data.data.list.map { origin ->
                    VideoSearchBean.NaifeiBean(
                        origin.vod_id,
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