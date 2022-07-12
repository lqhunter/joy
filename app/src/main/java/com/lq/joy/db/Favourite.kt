package com.lq.joy.db

import androidx.room.Entity
import androidx.room.PrimaryKey


const val TABLE_FAVOURITE = "Favourite"

@Entity(tableName = TABLE_FAVOURITE)
data class Favourite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    //资源类型，SourceType.SAKURA.ordinal
    val type: Int,
    //唯一标识
    val uniqueTag: String,
    //跳转详情用
    val jumpKey: String,
    //封面
    val coverUrl: String,
    //名字
    val name: String,
    //更新的时间点
    val updateTime: Long
)
