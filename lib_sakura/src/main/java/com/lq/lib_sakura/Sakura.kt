package com.lq.lib_sakura

import android.content.Context
import com.lq.lib_sakura.bean.HomeBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

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

    /**
     * 调试阶段用本地html，避免多次请求接口
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getLocalHomeData(context: Context): HomeBean? {
        return withContext(Dispatchers.IO) {

            delay(3000)
            var inputStream: InputStream? = null
            var reader:InputStreamReader? = null
            try {
                inputStream = context.assets.open("home.html")
                reader = InputStreamReader(inputStream)
                val html = reader.readText()
                Converter.parseHome(html)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } finally {
                try {
                    inputStream?.close()
                    reader?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


}