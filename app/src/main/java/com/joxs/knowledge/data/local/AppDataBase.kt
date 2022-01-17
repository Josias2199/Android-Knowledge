package com.joxs.knowledge.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joxs.knowledge.data.local.dao.MovieDao
import com.joxs.knowledge.data.local.entity.MovieEntity

@Database(entities = [MovieEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}