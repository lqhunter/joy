package com.lq.joy.data.sakura.bean

data class DetailBean(
    val animationName: String,
    val coverUrl: String,
    val score: String,
    val episodes: List<PlayBean>
)

data class PlayBean(
    val episodeName: String,
    val playHtmlUrl: String? = null,
    val playUrl: String? = null
)