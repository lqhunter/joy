package com.lq.joy.data.sakura

import android.content.Context
import com.lq.joy.data.BaseResult
import com.lq.lib_sakura.Sakura
import com.lq.lib_sakura.bean.HomeBean

class SakuraRepository(val context: Context) : ISakuraRepository {


    override suspend fun getHomeData(): BaseResult<HomeBean> {
//        val bean = Sakura.getHomeData()

        val bean = Sakura.getLocalHomeData(context)

        return if (bean == null) {
            BaseResult.Error(IllegalStateException())
        } else {
            BaseResult.Success(bean)
        }
    }
}