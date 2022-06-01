package com.lq.joy.data.sakura

import com.lq.joy.data.BaseResult
import com.lq.joy.data.sakura.bean.DetailBean
import com.lq.joy.data.sakura.bean.HomeBean

interface ISakuraRepository {

    suspend fun getHomeData(): BaseResult<HomeBean>

    suspend fun getDetailData():BaseResult<DetailBean>
}