package com.lq.joy.data.sakura.bean

data class HomeBean(val banners: List<HomeBannerBean>, val groups: List<HomeGroupBean>)

data class HomeBannerBean(
    //show/5519.html解析出id
    val id: String,
    val name: String,
    val coverUrl: String,
    val updateTime: String,
    val detailUrl: String
)

data class HomeGroupBean(
    val groupTitle: String,
    val groupUrl: String,
    val items: List<HomeItemBean>
)

data class HomeItemBean(
    val id: String,
    val name: String,
    val coverUrl: String,
    val newestEpisode: String? = null,
    val newestEpisodeUrl: String? = null,
    val detailUrl: String,
    val tags: List<Tag>? = null
)

//path: "/143/" eg.http://www.yinghuacd.com/143/
data class Tag(val name: String, val path: String)