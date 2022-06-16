package com.lq.joy.data.sakura.bean

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
    val playUrl: String? = null,
    val playHtmlUrl: String? = null,
)