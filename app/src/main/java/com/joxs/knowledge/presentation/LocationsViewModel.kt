package com.joxs.knowledge.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.joxs.knowledge.data.local.entity.UserLocation
import com.joxs.knowledge.data.network.Resource
import com.joxs.knowledge.domain.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val locationsRepository: LocationsRepository
): ViewModel() {

    fun saveCurrentLocation(currentLocation: UserLocation) = liveData(Dispatchers.IO){
        emit(Resource.Loading(data = null))
        try{
            emit(Resource.Success(data = locationsRepository.saveCurrentLocation(currentLocation)))
        }catch (throwable: Throwable){
            emit(Resource.Error(data = null, throwable = throwable))
        }
    }

    fun getCurrentLocation(): LiveData<List<UserLocation>> = locationsRepository.getCurrentLocation()

}