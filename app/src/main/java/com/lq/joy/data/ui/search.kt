package com.lq.joy.data.ui

import com.lq.joy.data.netfix.bean.NaifeiSearchItem


sealed class SearchBean {
    data class Title(val title: String) : SearchBean()

    data class NaifeiBean(val origin: NaifeiSearchItem) : SearchBean() {
        val name = origin.vod_name
        val coverUrl = origin.vod_pic
        val area: String = origin.vod_area
        val type: String = origin.vod_class
        val remarks: String = origin.vod_remarks
        val score: String = origin.vod_douban_score
    }

}
