package com.lq.joy.data.netfix.bean

data class NaifeiSearchBean(
    val code: Int,
    val `data`: Data,
    val msg: String
) {
    data class Data(
        val limit: Int,
        val list: List<NaifeiSearchItem>,
        val page: Int,
        val total: Int
    ) {
        data class NaifeiSearchItem(
            val group_id: Int,
            val type: Type,
            val type_1: Type1,
            val type_id: Int,
            val type_id_1: Int,
            val vod_actor: String,
            val vod_area: String,
            val vod_author: String,
            val vod_behind: String,
            val vod_blurb: String,
            val vod_class: String,
            val vod_color: String,
            val vod_content: String,
            val vod_copyright: Int,
            val vod_director: String,
            val vod_douban_id: Int,
            val vod_douban_score: String,
            val vod_down: Int,
            val vod_down_from: String,
            val vod_down_note: String,
            val vod_down_server: String,
            val vod_down_url: String,
            val vod_duration: String,
            val vod_en: String,
            val vod_hits: Int,
            val vod_hits_day: Int,
            val vod_hits_month: Int,
            val vod_hits_week: Int,
            val vod_id: Int,
            val vod_isend: Int,
            val vod_jumpurl: String,
            val vod_lang: String,
            val vod_letter: String,
            val vod_level: Int,
            val vod_lock: Int,
            val vod_name: String,
            val vod_pic: String,
            val vod_pic_screenshot: String,
            val vod_pic_slide: String,
            val vod_pic_thumb: String,
            val vod_play_from: String,
            val vod_play_note: String,
            val vod_play_server: String,
            val vod_play_url: String,
            val vod_plot: Int,
            val vod_plot_detail: String,
            val vod_plot_name: String,
            val vod_points: Int,
            val vod_points_down: Int,
            val vod_points_play: Int,
            val vod_pubdate: String,
            val vod_pwd: String,
            val vod_pwd_down: String,
            val vod_pwd_down_url: String,
            val vod_pwd_play: String,
            val vod_pwd_play_url: String,
            val vod_pwd_url: String,
            val vod_rel_art: String,
            val vod_rel_vod: String,
            val vod_remarks: String,
            val vod_reurl: String,
            val vod_score: String,
            val vod_score_all: Int,
            val vod_score_num: Int,
            val vod_serial: String,
            val vod_state: String,
            val vod_status: Int,
            val vod_sub: String,
            val vod_tag: String,
            val vod_time: Int,
            val vod_time_add: Int,
            val vod_time_hits: Int,
            val vod_time_make: Int,
            val vod_total: Int,
            val vod_tpl: String,
            val vod_tpl_down: String,
            val vod_tpl_play: String,
            val vod_trysee: Int,
            val vod_tv: String,
            val vod_up: Int,
            val vod_version: String,
            val vod_weekday: String,
            val vod_writer: String,
            val vod_year: String
        ) {
            data class Type(
                val type_1: Type1,
                val type_des: String,
                val type_en: String,
                val type_extend: TypeExtend,
                val type_id: Int,
                val type_jumpurl: String,
                val type_key: String,
                val type_logo: String,
                val type_mid: Int,
                val type_name: String,
                val type_pic: String,
                val type_pid: Int,
                val type_sort: Int,
                val type_status: Int,
                val type_title: String,
                val type_tpl: String,
                val type_tpl_detail: String,
                val type_tpl_down: String,
                val type_tpl_list: String,
                val type_tpl_play: String,
                val type_union: String
            ) {
                data class Type1(
                    val childids: String,
                    val type_des: String,
                    val type_en: String,
                    val type_extend: TypeExtend,
                    val type_id: Int,
                    val type_jumpurl: String,
                    val type_key: String,
                    val type_logo: String,
                    val type_mid: Int,
                    val type_name: String,
                    val type_pic: String,
                    val type_pid: Int,
                    val type_sort: Int,
                    val type_status: Int,
                    val type_title: String,
                    val type_tpl: String,
                    val type_tpl_detail: String,
                    val type_tpl_down: String,
                    val type_tpl_list: String,
                    val type_tpl_play: String,
                    val type_union: String
                ) {
                    data class TypeExtend(
                        val area: String,
                        val `class`: String,
                        val director: String,
                        val lang: String,
                        val star: String,
                        val state: String,
                        val version: String,
                        val year: String
                    )
                }

                data class TypeExtend(
                    val area: String,
                    val `class`: String,
                    val director: String,
                    val lang: String,
                    val star: String,
                    val state: String,
                    val version: String,
                    val year: String
                )
            }

            data class Type1(
                val childids: String,
                val type_des: String,
                val type_en: String,
                val type_extend: TypeExtend,
                val type_id: Int,
                val type_jumpurl: String,
                val type_key: String,
                val type_logo: String,
                val type_mid: Int,
                val type_name: String,
                val type_pic: String,
                val type_pid: Int,
                val type_sort: Int,
                val type_status: Int,
                val type_title: String,
                val type_tpl: String,
                val type_tpl_detail: String,
                val type_tpl_down: String,
                val type_tpl_list: String,
                val type_tpl_play: String,
                val type_union: String
            ) {
                data class TypeExtend(
                    val area: String,
                    val `class`: String,
                    val director: String,
                    val lang: String,
                    val star: String,
                    val state: String,
                    val version: String,
                    val year: String
                )
            }
        }
    }
}