package com.joxs.knowledge.domain

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import javax.inject.Inject

class ProfileRepository @Inject constructor(){

    private val storage  = Firebase.storage("gs://knowledge-2baa8.appspot.com")
    private val storageRef = storage.reference
    var imagesRef: StorageReference? = storageRef.child("images")
    var profileRef = storageRef.child("images/profileImage.jpg")
    val path = profileRef.path
    val name = profileRef.name

    fun uploadImage(imageBytes: ByteArray){

        imagesRef = profileRef.parent
        val uploadTask = profileRef.putBytes(imageBytes)
        uploadTask.addOnSuccessListener {
            Log.d("Firestorage", "Succes image upload")
        }.addOnFailureListener { taskSnapshot ->
            Log.d("Firestorage", "Error image upload")
            throw taskSnapshot
        }
    }

    fun getUpdateImage(): LiveData<Uri> {
        val imageUrl = MutableLiveData<Uri>()
        storageRef.child("images/profileImage.jpg").downloadUrl.addOnSuccessListener {
            imageUrl.value = it
            Log.d("Firestorage", "Success get update image")
        }.addOnFailureListener {
            // Handle any errors
            Log.d("Firestorage", "Error get update image")
        }
        return imageUrl
    }

}