package com.joxs.knowledge.domain

import androidx.room.withTransaction
import com.joxs.knowledge.data.local.AppDatabase
import com.joxs.knowledge.data.local.entity.MovieEntity
import com.joxs.knowledge.data.network.Resource
import com.joxs.knowledge.data.network.networkBoundResource
import com.joxs.knowledge.data.remote.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MoviesRepository @Inject constructor(
    private val apiService: ApiService,
    private val db: AppDatabase
    ) {

    private val movieDao = db.movieDao()

    fun getPopularMovies() : Flow<Resource<List<MovieEntity>>> = networkBoundResource(
        query = {
            movieDao.loadMovies()
        },
        fetch = {
            delay(2000)
            apiService.getPopularMovies(1)
        },
        saveFetchResult = { result ->
            db.withTransaction {
                movieDao.deleteAllMovies()
                movieDao.saveMovies(result.movies)
            }
        }
    )

}