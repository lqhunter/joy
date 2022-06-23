package com.lq.joy.data.netfix

import com.lq.joy.data.Api
import com.lq.joy.data.netfix.bean.NaifeiDetailBean
import com.lq.joy.data.netfix.bean.NaifeiSearchBean
import retrofit2.http.GET
import retrofit2.http.Query

interface NaifeiService {

    @GET(Api.NAIFEI_ORG_SEARCH)
    suspend fun search(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("wd") wd: String
    ): NaifeiSearchBean

    @GET(Api.NAIFEI_ORG_DETAIL)
    suspend fun detail(@Query("vod_id") id: Int): NaifeiDetailBean
}