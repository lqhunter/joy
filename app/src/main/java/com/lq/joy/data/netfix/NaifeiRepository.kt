package com.lq.joy.data.netfix

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lq.joy.data.Api
import com.lq.joy.data.netfix.bean.NaifeiDetailBean
import com.lq.joy.data.ui.VideoSearchBean
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NaifeiRepository : INaifeiRepository {

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Api.NAIFEI_HOST)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service: NaifeiService by lazy {
        retrofit.create(NaifeiService::class.java)
    }


    override fun search(
        limit: Int,
        wd: String
    ): Flow<PagingData<VideoSearchBean>> {
        return Pager(PagingConfig(limit)) {
            NaifeiSearchSource(service, wd)
        }.flow
    }

    override suspend fun detail(vodId: Int): NaifeiDetailBean {
        return service.detail(vodId)
    }
}