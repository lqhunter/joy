package com.lq.joy.data.netfix

import androidx.paging.*
import com.google.gson.Gson
import com.lq.joy.JoyApplication
import com.lq.joy.data.netfix.bean.NaifeiDetailBean
import com.lq.joy.data.netfix.bean.NaifeiSearchBean
import com.lq.joy.data.ui.PlayBean
import com.lq.joy.data.ui.VideoSearchBean
import com.lq.joy.data.ui.VideoSource
import com.lq.joy.utils.getLocalString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

class FakeNaifeiRepository : INaifeiRepository {
    override fun search(limit: Int, wd: String): Flow<PagingData<VideoSearchBean>> {
        return Pager(PagingConfig(limit)) {
            FakeSource(wd)
        }.flow
    }

    override suspend fun detail(vodId: Int): NaifeiDetailBean {
        delay(1000)
        return fakeDetailData()
    }

    private fun fakeDetailData(): NaifeiDetailBean {
        val json = getLocalString(JoyApplication.context, "naifeidetail.json")
        val gson = Gson()
        return gson.fromJson(json, NaifeiDetailBean::class.java)
    }
}

private class FakeSource(private val searchKey: String) : PagingSource<Int, VideoSearchBean>() {
    override fun getRefreshKey(state: PagingState<Int, VideoSearchBean>): Int = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoSearchBean> {
        return try {
            val currentPage = params.key ?: 1
            delay(1000)
            val data = fakeSearchData()

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
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    private fun fakeSearchData(): NaifeiSearchBean? {
        val json = getLocalString(JoyApplication.context, "search.json")
        val gson = Gson()
        return gson.fromJson(json, NaifeiSearchBean::class.java)
    }
}