package com.lq.joy.data.ui

import com.lq.joy.data.sakura.bean.PlayBean
import java.io.Serializable

sealed class SearchBean : Serializable {

    data class Title(val title: String) : SearchBean()

    data class NaifeiBean(
        val name: String,
        val coverUrl: String,
        val area: String,
        val type: String,
        val remarks: String,
        val score: String,
        val playBean: List<PlayBean>
    ) : SearchBean()

}
