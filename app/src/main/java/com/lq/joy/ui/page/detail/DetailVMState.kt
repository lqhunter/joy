package com.lq.joy.ui.page.detail

import com.lq.joy.data.Api
import com.lq.joy.data.Api.NAIFEI_HOST
import com.lq.joy.data.SourceType
import com.lq.joy.data.netfix.bean.NaifeiDetailBean
import com.lq.joy.data.ui.DetailBean
import com.lq.joy.data.ui.PlayBean
import com.lq.joy.data.ui.RecommendBean
import com.lq.joy.data.ui.VideoSource
import com.lq.joy.db.Favourite

data class DetailVMState(
    val sourceType: SourceType,
    val isLoading: Boolean = false,
    val currentSourceIndex: Int = 0,
    val currentEpisodeIndex: Int = -1,
    val isPlaying: Boolean = false,
    val favourite: Favourite? = null,
    val naifeiDetailBean: NaifeiDetailBean? = null,
    val sakuraDetailBean: DetailBean? = null
) {
    fun toUIState(): DetailUiState {
        if (sourceType == SourceType.NAIFEI) {
            return if (naifeiDetailBean == null) {
                DetailUiState.NoData(isLoading = isLoading)
            } else {
                DetailUiState.HasData(
                    isLoading = isLoading,
                    isPlaying = isPlaying,
                    isFavorite = favourite != null,
                    videoSource = mutableListOf<VideoSource>().apply {
                        naifeiDetailBean.data.vod_play_list.forEachIndexed { index, vodPlay ->
                            add(VideoSource("线路${index}", mutableListOf<PlayBean>().apply {
                                vodPlay.urls.forEach {
                                    add(PlayBean(it.name, it.url))
                                }
                            }))
                        }
                    },
                    currentEpisodeIndex = currentEpisodeIndex,
                    currentSourceIndex = currentSourceIndex,
                    coverUrl = NAIFEI_HOST + "/" + naifeiDetailBean.data.vod_pic,
                    name = naifeiDetailBean.data.vod_name,
                    score = naifeiDetailBean.data.vod_score,
                    recommend = mutableListOf<RecommendBean>().apply {
                        naifeiDetailBean.data.rel_vods.forEach {
                            add(
                                RecommendBean.NaifeiRecommend(
                                    cover = Api.NAIFEI_HOST + "/" + it.vod_pic,
                                    name = it.vod_name,
                                    tag = it.vod_tag,
                                    id = it.vod_id
                                )
                            )
                        }
                    }
                )
            }
        } else if (sourceType == SourceType.SAKURA) {
            return if (sakuraDetailBean == null) {
                DetailUiState.NoData(isLoading = isLoading)
            } else {
                DetailUiState.HasData(
                    isLoading = isLoading,
                    isPlaying = isPlaying,
                    isFavorite = favourite != null,
                    videoSource = mutableListOf(
                        VideoSource("线路1", sakuraDetailBean.episodes)
                    ),
                    currentEpisodeIndex = currentEpisodeIndex,
                    currentSourceIndex = currentSourceIndex,
                    coverUrl = sakuraDetailBean.coverUrl,
                    name = sakuraDetailBean.animationName,
                    score = sakuraDetailBean.score,
                    recommend = mutableListOf<RecommendBean>().apply {
                        sakuraDetailBean.recommend.forEach {
                            add(
                                RecommendBean.SakuraRecommend(
                                    cover = it.coverUrl,
                                    name = it.name,
                                    tag = it.tags.let { tag ->
                                        val sb = StringBuilder()
                                        tag?.forEach { t ->
                                            sb.append(t.name).append(" ")
                                        }
                                        sb.toString()
                                    },
                                    htmlUrl = it.detailUrl
                                )
                            )
                        }
                    }
                )
            }
        } else {
            return DetailUiState.NoData(isLoading = false)
        }
    }

    fun updateUrl(index: Int, playUrl: String): DetailVMState {
        return copy(sakuraDetailBean = sakuraDetailBean?.episodes?.let {
            val old = it[index]
            val new = old.copy(playUrl = playUrl)

            val changeList = it.toMutableList()
            changeList.remove(changeList[index])
            changeList.add(index, new)
            return@let sakuraDetailBean.copy(episodes = changeList)
        })
    }
}

sealed interface DetailUiState {
    val isLoading: Boolean

    data class NoData(
        override val isLoading: Boolean
    ) : DetailUiState

    data class HasData(
        override val isLoading: Boolean,
        val isPlaying: Boolean,
        val isFavorite: Boolean,
        val videoSource: List<VideoSource>,
        val currentEpisodeIndex: Int,
        val currentSourceIndex: Int,
        val coverUrl: String,
        val name: String,
        var score: String,
        val recommend: List<RecommendBean>
    ) : DetailUiState
}