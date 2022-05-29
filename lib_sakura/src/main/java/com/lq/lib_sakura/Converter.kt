package com.lq.lib_sakura

import com.lq.lib_sakura.bean.HomeBannerBean
import com.lq.lib_sakura.bean.HomeBean
import com.lq.lib_sakura.bean.HomeGroupBean
import com.lq.lib_sakura.bean.HomeItemBean
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode

object Converter {

    fun parseHome(html: String): HomeBean {
        println("lq-- parseHome")

        val document = Jsoup.parse(html)
        val heros = document.getElementsByClass("heros")
        val banners = mutableListOf<HomeBannerBean>()
        val group = mutableListOf<HomeGroupBean>()
        println("lq-- heros:${heros.size}")

        heros.forEach { hero ->
            val lis = hero.select("li")
            println("lq-- lis:${lis.size}")

            lis.forEach { li ->
                val arrt = li.getElementsByTag("a")[0].attributes()
                val name = arrt["title"]
                val url = arrt["href"]
                val cover = li.getElementsByTag("img")[0].attributes()["src"]

                val childNodes = li.getElementsByTag("em")[0].childNodes()
                var updateTime = ""
                if (childNodes.isNotEmpty()) {
                    updateTime = (li.getElementsByTag("em")[0].childNode(0) as TextNode).text()
                }
                banners.add(HomeBannerBean(url.substring(6, url.length - 5), name, cover, updateTime, url))
            }
        }

        val content = document.getElementsByClass("firs l")[0]
        val dtit = content.getElementsByClass("dtit")
        val img = content.getElementsByClass("img")

        for (i in 0 until dtit.size) {
            val a = dtit[i].getElementsByTag("a")[0]
            val url = a.attributes()["href"]
            val title = (a.childNode(0) as TextNode).text()

            val groups = mutableListOf<HomeItemBean>()
            val groupBean = HomeGroupBean(title, url, groups)

            val lis = img[i].getElementsByTag("li")
            lis.forEach { li ->

                val url = li.getElementsByTag("a")[0].attributes()["href"]
                val attributes = li.getElementsByTag("img")[0].attributes()
                val cover = attributes["src"]
                val name =  attributes["alt"]

                val newests = li.getElementsByAttribute("target")
                var newestUrl = ""
                var newestEpisode = ""
                if (newests.isNotEmpty()) {
                    newestUrl = newests[0].attributes()["href"]
                    newestEpisode = (newests[0].childNode(0) as TextNode).text()
                }
                groups.add(HomeItemBean(url.substring(6, url.length - 5), name, cover, newestEpisode, newestUrl, url))
            }
            group.add(groupBean)
        }
        return HomeBean(banners, group)
    }
}