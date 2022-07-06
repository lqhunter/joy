package com.lq.joy.data.ui

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

data class RecommendBean(val cover: String, val name: String, val tag: String)