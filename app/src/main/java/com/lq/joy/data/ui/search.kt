package com.lq.joy.data.ui

import com.lq.joy.data.sakura.bean.Tag
import com.lq.joy.data.sakura.bean.VideoSource
import java.io.Serializable

sealed class VideoSearchBean : Serializable {

    data class NaifeiBean(
        val vodId:Int,
        val name: String,
        val coverUrl: String,
        val area: String,
        val type: String,
        val remarks: String,
        val score: String,
        val videoSources: List<VideoSource>
    ) : VideoSearchBean()

    data class SakuraBean(
        val id: String,
        val name: String,
        val coverUrl: String,
        val newestEpisode: String? = null,
        val newestEpisodeUrl: String? = null,
        val detailUrl: String,
        val tags: List<Tag>? = null
    ) : VideoSearchBean()


}
