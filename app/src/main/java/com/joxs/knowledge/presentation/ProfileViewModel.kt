package com.joxs.knowledge.presentation


import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.joxs.knowledge.data.network.Resource
import com.joxs.knowledge.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    fun uploadImage(imageBytes: ByteArray) = liveData(Dispatchers.IO){
        emit(Resource.Loading(data = null))
        try{
            emit(Resource.Success(data = profileRepository.uploadImage(imageBytes)))
        }catch (throwable: Throwable){
            emit(Resource.Error(data = null, throwable = throwable))
        }
    }

    fun getUpdateImage(): LiveData<Uri> = profileRepository.getUpdateImage()



}