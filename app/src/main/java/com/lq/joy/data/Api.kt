package com.lq.joy.data


object Api {

    const val HOME = "http://www.yinghuacd.com"
    const val DETAIL = "http://www.yinghuacd.com/show/2.html"
    const val SEARCH = "http://www.yinghuacd.com/search/"

    const val NAIFEI_HOST = "https://www.naifei.org"

    //https://www.naifei.org/api.php/v1.vod?page=1&limit=10&wd=海贼王
    const val NAIFEI_ORG_SEARCH = "/api.php/v1.vod"
    const val NAIFEI_ORG_DETAIL = "/api.php/v1.vod/detail"

}

/**
 * 只增不减，顺序不要换，否则数据库中存储的数据会乱
 */
enum class SourceType(val netName: String) {
    SAKURA("樱花动漫"),
    NAIFEI("奈飞"),
}