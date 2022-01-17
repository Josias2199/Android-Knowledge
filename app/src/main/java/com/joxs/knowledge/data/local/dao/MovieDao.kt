package com.joxs.knowledge.data.local.dao

import com.joxs.knowledge.data.local.entity.MovieEntity

import androidx.room.OnConflictStrategy
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun loadMovies(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend  fun saveMovies(movieEntityList: List<MovieEntity>)

    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()
}