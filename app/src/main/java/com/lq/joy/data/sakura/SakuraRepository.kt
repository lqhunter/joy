package com.lq.joy.data.sakura

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lq.joy.data.BaseResult
import com.lq.joy.data.sakura.bean.HomeBean
import com.lq.joy.data.ui.DetailBean
import com.lq.joy.data.ui.VideoSearchBean
import kotlinx.coroutines.flow.Flow

class SakuraRepository : ISakuraRepository {

    override suspend fun getHomeData(): BaseResult<HomeBean> {
        val bean = SakuraService.getHomeData()
        return if (bean == null) {
            BaseResult.Error(IllegalStateException())
        } else {
            BaseResult.Success(bean)
        }
    }

    override suspend fun getDetailData(url: String): BaseResult<DetailBean> {
        val bean = SakuraService.getDetailData(url)
        return if (bean == null) {
            BaseResult.Error(IllegalStateException())
        } else {
            BaseResult.Success(bean)
        }
    }

    override suspend fun getPlayUrl(url: String): BaseResult<String> {
        val bean = SakuraService.getVideoPath(url)
        return if (bean == null) {
            BaseResult.Error(IllegalStateException())
        } else {
            BaseResult.Success(bean)
        }
    }

    override fun search(key: String): Flow<PagingData<VideoSearchBean>> {
        return Pager(PagingConfig(20)) {
            SakuraSearchSource(key)
        }.flow
    }
}