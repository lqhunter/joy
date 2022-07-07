package com.lq.joy.data.ui

import com.lq.joy.data.SourceType
import com.lq.joy.data.sakura.bean.HomeItemBean

data class DetailBean(
    val animationName: String,
    val coverUrl: String,
    val score: String,
    val episodes: List<PlayBean>,
    val recommend: List<HomeItemBean>
)

/**
 * 线路
 */
data class VideoSource(val sourceName: String, val episodes: List<PlayBean>)

data class PlayBean(
    val episodeName: String,
    val playUrl: String
)

sealed class RecommendBean(open val cover: String, open val name: String, open val tag: String) {
    data class NaifeiRecommend(
        override val cover: String,
        override val name: String,
        override val tag: String,
        val id: Int
    ) : RecommendBean(cover, name, tag)

    data class SakuraRecommend(
        override val cover: String,
        override val name: String,
        override val tag: String,
        val htmlUrl: String
    ) : RecommendBean(cover, name, tag)
}