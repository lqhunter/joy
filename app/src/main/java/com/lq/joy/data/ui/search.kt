package com.lq.joy.data.ui

import com.lq.joy.data.netfix.bean.NaifeiSearchBean
import com.lq.joy.data.sakura.bean.SakuraSearchBean

enum class SOURCE {
    SAKURA,
    NAIFEI_ORG
}

sealed class SearchGroup(val source: SOURCE) {
    data class Sakura(
        val size: Int,
        val items: List<SakuraSearchBean>
    ) : SearchGroup(SOURCE.SAKURA)

    data class NaifeiGroup(val size: Int, val item: NaifeiSearchBean) :
        SearchGroup(SOURCE.NAIFEI_ORG)
}
