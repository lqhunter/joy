package com.lq.joy.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Favourite::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}