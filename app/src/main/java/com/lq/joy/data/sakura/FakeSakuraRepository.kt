package com.lq.joy.data.sakura

import android.content.Context
import androidx.paging.*
import com.google.gson.Gson
import com.lq.joy.JoyApplication
import com.lq.joy.data.BaseResult
import com.lq.joy.data.netfix.bean.NaifeiSearchBean
import com.lq.joy.data.sakura.bean.DetailBean
import com.lq.joy.data.sakura.bean.HomeBean
import com.lq.joy.data.ui.VideoSearchBean
import com.lq.joy.utils.getLocalString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

class FakeSakuraRepository(private val context: Context) : ISakuraRepository {

    override suspend fun getHomeData(): BaseResult<HomeBean> {
        val bean = SakuraService.getLocalHomeData(context)

        return if (bean == null) {
            BaseResult.Error(IllegalStateException())
        } else {
            BaseResult.Success(bean)
        }
    }

    override suspend fun getDetailData(url: String): BaseResult<DetailBean> {
        val bean = SakuraService.getLocalDetailData(context)

        return if (bean == null) {
            BaseResult.Error(IllegalStateException())
        } else {
            BaseResult.Success(bean)
        }
    }

    override suspend fun getPlayUrl(url: String): BaseResult<String> {
        return BaseResult.Success("https://yun.66dm.net/SBDM/OtomeGameSekai01.m3u8")
    }

    override fun search(key: String): Flow<PagingData<VideoSearchBean>> {
        return Pager(PagingConfig(20)) {
            FakeSource(context)
        }.flow
    }


    private class FakeSource(private val context: Context) : PagingSource<Int, VideoSearchBean>() {
        override fun getRefreshKey(state: PagingState<Int, VideoSearchBean>): Int = 1

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoSearchBean> {
            return try {
                val currentPage = params.key ?: 1
                delay(1000)
                val data = SakuraService.getLocalSearchData(context = context)

                if (data != null) {
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

                    LoadResult.Page(
                        data = result,
                        prevKey = null,
                        nextKey = null
                    )
                } else {
                    LoadResult.Error(RuntimeException(""))
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

}