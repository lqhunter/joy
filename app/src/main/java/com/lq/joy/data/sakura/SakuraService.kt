package com.lq.joy.data.sakura

import android.content.Context
import com.lq.joy.data.Api
import com.lq.joy.data.sakura.bean.DetailBean
import com.lq.joy.data.sakura.bean.HomeBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object SakuraService {

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

    /**
     * 调试阶段用本地html，避免多次请求接口
     */
    suspend fun getLocalHomeData(context: Context): HomeBean? {
        return withContext(Dispatchers.IO) {

            delay(1000)

            getLocalString(context, "home.html")?.let {
                Converter.parseHome(it)
            }
        }
    }

    suspend fun getLocalDetailData(context: Context): DetailBean? {
        return withContext(Dispatchers.IO) {

            delay(1000)

            getLocalString(context, "detail.html")?.let {
                Converter.parseDetail(it)
            }
        }
    }


    private fun getLocalString(context: Context, fileName: String): String? {
        var inputStream: InputStream? = null
        var reader: InputStreamReader? = null
        try {
            inputStream = context.assets.open(fileName)
            reader = InputStreamReader(inputStream)
            return reader.readText()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }


}