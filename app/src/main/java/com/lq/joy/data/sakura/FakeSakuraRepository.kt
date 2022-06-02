package com.lq.joy.data.sakura

import android.content.Context
import com.lq.joy.data.BaseResult
import com.lq.joy.data.sakura.bean.DetailBean
import com.lq.joy.data.sakura.bean.HomeBean

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
        return BaseResult.Success("")
    }
}