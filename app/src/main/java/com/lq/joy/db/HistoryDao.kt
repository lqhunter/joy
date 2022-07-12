package com.lq.joy.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.lq.joy.data.SourceType
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM $TABLE_FAVOURITE")
    fun getFavouriteList(): PagingSource<Int, Favourite>

    @Query("SELECT * FROM $TABLE_FAVOURITE WHERE type = :type AND uniqueTag = :uniqueTag")
    fun getFavourite(type: Int, uniqueTag: String): Flow<Favourite?>

    @Insert
    suspend fun addFavourite(favourite: Favourite)

    @Delete
    suspend fun deleteFavourite(favourite: Favourite)
}