package com.joxs.knowledge.data.remote

import com.joxs.knowledge.data.remote.model.MoviesResponse
import javax.inject.Inject

class ApiHelper @Inject constructor(private  val apiService: ApiService) {
    suspend fun getPopularMovies(page: Int): MoviesResponse = apiService.getPopularMovies(page)
    suspend fun getTopRatedMovies(page: Int): MoviesResponse = apiService.getTopRatedMovies(page)
    suspend fun getUpComingMovies(page: Int): MoviesResponse = apiService.getUpComingMovies(page)
}