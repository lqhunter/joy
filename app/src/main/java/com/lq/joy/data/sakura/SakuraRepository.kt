package com.lq.joy.data.sakura

import com.lq.joy.data.BaseResult
import com.lq.joy.data.sakura.bean.DetailBean
import com.lq.joy.data.sakura.bean.HomeBean

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
}