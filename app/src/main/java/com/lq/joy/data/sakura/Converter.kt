package com.lq.joy.data.sakura

import com.lq.joy.data.sakura.bean.*
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
import java.lang.NumberFormatException

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
                banners.add(
                    HomeBannerBean(
                        url.substring(
                            6,
                            url.length - 5
                        ), name, cover, updateTime, url
                    )
                )
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
                val name = attributes["alt"]

                val newests = li.getElementsByAttribute("target")
                var newestUrl: String? = null
                var newestEpisode: String? = null
                if (newests.isNotEmpty()) {
                    newestUrl = newests[0].attributes()["href"]
                    newestEpisode = (newests[0].childNode(0) as TextNode).text()
                }
                groups.add(
                    HomeItemBean(
                        url.substring(
                            6,
                            url.length - 5
                        ), name, cover, newestEpisode, newestUrl, url
                    )
                )
            }
            group.add(groupBean)
        }
        return HomeBean(banners, group)
    }

    fun parseDetail(html: String): DetailBean? {
        val document = Jsoup.parse(html)
        var score = "--"
        val scoreClass = document.getElementsByClass("score")
        if (scoreClass.isNotEmpty()) {
            val emClass = scoreClass[0].getElementsByTag("em")
            if (emClass.isNotEmpty()) {
                score = (emClass[0].childNode(0) as TextNode).text()
            }
        }

        var result: DetailBean? = null
        val list = mutableListOf<PlayBean>()
        val recommend = mutableListOf<HomeItemBean>()

        val thumb = document.getElementsByClass("thumb l")
        if (thumb.isNotEmpty()) {
            val attrs = thumb[0].getElementsByTag("img")[0].attributes()
            result = DetailBean(attrs["alt"], attrs["src"], score, list, recommend)
        }

        val movurl = document.getElementsByClass("movurl")
        if (movurl.isNotEmpty()) {
            movurl[0].getElementsByTag("a").forEach {
                val playHtmlUrl = it.attributes()["href"]
                val episodeName = (it.childNode(0) as TextNode).text()
                list.add(PlayBean(episodeName, playHtmlUrl))
            }
        }

        val pics = document.getElementsByClass("pics")
        if (pics.isNotEmpty()) {
            val lis = pics[0].getElementsByTag("li")
            lis.forEach { li ->
                val attributes = li.getElementsByTag("img")[0].attributes()
                val cover = attributes["src"]
                val name = attributes["alt"]
                val detailUrl = li.getElementsByAttribute("href")[0].attributes()["href"]
                val newest = (li.getElementsByTag("font")[0].childNode(0) as TextNode).text()
                val tags = mutableListOf<Tag>()
                var types = li.getElementsByAttribute("target")
                types.forEach { e ->
                    tags.add(Tag(name = (e.childNode(0) as TextNode).text(), path = e.attributes()["href"]))
                }

                recommend.add(
                    HomeItemBean(
                        detailUrl.substring(6, detailUrl.length - 5),
                        name,
                        cover,
                        newestEpisode = newest,
                        detailUrl = detailUrl,
                        tags = tags
                    )
                )
            }
        }


        return result
    }

    fun parsePlayPath(html: String): String? {
        val document = Jsoup.parse(html)
        val bofang = document.getElementsByClass("bofang")
        if (bofang.isNotEmpty()) {
            val dataVid = bofang[0].getElementsByAttribute("data-vid")[0]
            val url = dataVid.attributes()["data-vid"]
            return if (url.contains("$")) {
                url.substring(0, url.indexOf("$"))
            } else url
        }
        return null
    }

    fun parseSearchBean(html:String):SearchBean? {
        val document = Jsoup.parse(html)

        val list = mutableListOf<HomeItemBean>()
        val result = SearchBean(0, list)
        val pages = document.getElementsByClass("pages")
        if (pages.isNotEmpty()) {
            val lastn = pages[0].getElementById("lastn")
            if (lastn != null) {
                val page = (lastn.childNode(0) as TextNode).text()
                try {
                    result.totalPage = page.toInt()
                } catch (e:NumberFormatException) {
                    e.printStackTrace()
                }
            }
        }
        val lpic = document.getElementsByClass("lpic")[0]
        val lis = lpic.getElementsByTag("li")
        lis.forEach { li ->
            val attributes = li.getElementsByTag("img")[0].attributes()
            val cover = attributes["src"]
            val name = attributes["alt"]
            val detailUrl = li.getElementsByAttribute("href")[0].attributes()["href"]
            val fonts = li.getElementsByTag("span")[0].getElementsByTag("font")[0]
            var newest = ""
            if (fonts.childNodeSize() > 0) {
                newest = (fonts.childNode(0) as TextNode).text()
            }
            val tags = mutableListOf<Tag>()
            var types = li.getElementsByAttribute("target")
            types.forEach { e ->
                tags.add(Tag(name = (e.childNode(0) as TextNode).text(), path = e.attributes()["href"]))
            }

            list.add(
                HomeItemBean(
                    detailUrl.substring(6, detailUrl.length - 5),
                    name,
                    cover,
                    newestEpisode = newest,
                    detailUrl = detailUrl,
                    tags = tags
                )
            )
        }
        return result
    }
}