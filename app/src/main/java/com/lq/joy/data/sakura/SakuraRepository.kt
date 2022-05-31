package com.lq.joy.data.sakura

import android.content.Context
import com.lq.joy.data.BaseResult
import com.lq.joy.data.sakura.bean.HomeBean

class SakuraRepository(private val context: Context) : ISakuraRepository {


    override suspend fun getHomeData(): BaseResult<HomeBean> {
//        val bean = Sakura.getHomeData()

        val bean = SakuraService.getLocalHomeData(context)

        return if (bean == null) {
            BaseResult.Error(IllegalStateException())
        } else {
            BaseResult.Success(bean)
        }
    }
}