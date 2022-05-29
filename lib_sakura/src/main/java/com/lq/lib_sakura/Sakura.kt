package com.lq.lib_sakura

import com.lq.lib_sakura.bean.HomeBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

object Sakura {

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .build()
    }

    private fun getRequest(url: String) = Request.Builder()
        .url(url)
        .get()
        .build()


    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getHomeData(): HomeBean? {
        return withContext(Dispatchers.IO) {
            try {
                val response = okHttpClient.newCall(getRequest(Api.HOME)).execute()
                if (response.isSuccessful) {
                    response.body?.string()?.let {
                        Converter.parseHome(it)
                    }
                } else {
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

        }
    }

}