package com.lq.joy.data.netfix

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.lq.joy.JoyApplication
import com.lq.joy.data.netfix.bean.NaifeiSearchBean
import com.lq.joy.data.ui.PlayBean
import com.lq.joy.data.ui.VideoSearchBean
import com.lq.joy.data.ui.VideoSource
import com.lq.joy.utils.getLocalString

class NaifeiSearchSource(
    private val service: NaifeiService,
    private val searchKey: String
) : PagingSource<Int, VideoSearchBean>() {
    override fun getRefreshKey(state: PagingState<Int, VideoSearchBean>): Int = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoSearchBean> {
        return try {
            val currentPage = params.key ?: 1
            val data = service.search(currentPage, params.loadSize, searchKey)
//            delay(3000)
//            val data = fakeData()

            if (data.code == 200) {
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
                            val sources = mutableListOf<VideoSource>()
                            val groups = vod_play_url.split("$$$")
                            groups.forEachIndexed { index, g ->
                                val r = mutableListOf<PlayBean>()
                                val couples = g.split("#")
                                couples.forEach { c ->
                                    val split = c.split("$")
                                    if (split.size == 2 && split[1].startsWith("http")) {
                                        r.add(PlayBean(split[0], split[1]))
                                    }
                                }
                                sources.add(VideoSource("??????$index", r))
                            }
                            sources
                        }
                    )
                })

                LoadResult.Page(
                    data = result,
                    prevKey = null,
                    nextKey = if (((currentPage - 1) * params.loadSize + data.data.list.size) < data.data.total) currentPage + 1 else null
                )
            } else {
                LoadResult.Error(RuntimeException("???????????????????????????,code:${data?.code} msg:${data?.msg}"))
            }
        } catch (e : Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    private fun fakeData(): NaifeiSearchBean? {
        val json = getLocalString(JoyApplication.context, "search.json")
        val gson = Gson()
        return gson.fromJson(json, NaifeiSearchBean::class.java)
    }
}