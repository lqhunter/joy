package com.lq.joy.data.sakura

import androidx.paging.PagingData
import com.lq.joy.data.BaseResult
import com.lq.joy.data.sakura.bean.HomeBean
import com.lq.joy.data.ui.DetailBean
import com.lq.joy.data.ui.VideoSearchBean
import kotlinx.coroutines.flow.Flow

interface ISakuraRepository {

    suspend fun getHomeData(): BaseResult<HomeBean>

    suspend fun getDetailData(url:String):BaseResult<DetailBean>

    suspend fun getPlayUrl(url:String):BaseResult<String>

    fun search(key:String): Flow<PagingData<VideoSearchBean>>
}