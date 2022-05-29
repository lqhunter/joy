package com.lq.joy.data.sakura

import com.lq.joy.data.BaseResult
import com.lq.lib_sakura.Sakura
import com.lq.lib_sakura.bean.HomeBean
import kotlinx.coroutines.flow.MutableStateFlow

class SakuraRepository : ISakuraRepository {


    override suspend fun getHomeData(): BaseResult<HomeBean> {
        val bean = Sakura.getHomeData()
        return if (bean == null) {
            BaseResult.Error(IllegalStateException())
        } else {
            BaseResult.Success(bean)
        }
    }
}