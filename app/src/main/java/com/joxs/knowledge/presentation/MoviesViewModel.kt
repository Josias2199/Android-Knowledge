package com.joxs.knowledge.presentation

import androidx.lifecycle.*

import com.joxs.knowledge.domain.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
        moviesRepository: MoviesRepository
    ) : ViewModel() {

    val popularMovies = moviesRepository.getPopularMovies().asLiveData()

}