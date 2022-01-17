package com.joxs.knowledge.domain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.joxs.knowledge.data.local.entity.UserLocation
import javax.inject.Inject

class LocationsRepository @Inject constructor() {

    private val firestoreDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var locationCounter = 0

    fun saveCurrentLocation(currentLocation: UserLocation){
        val location = hashMapOf(
            "latitude" to currentLocation.latitude,
            "longitude" to currentLocation.longitude,
            "date" to currentLocation.date
        )
        firestoreDB.collection("location")
            .add(location)
            .addOnSuccessListener{ documentReference ->
                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }.addOnFailureListener{ e ->
                Log.w("Firestore", "Error adding location", e)
            }
    }

    fun getCurrentLocation(): LiveData<List<UserLocation>> {
        val currentLocation = MutableLiveData<List<UserLocation>>()
        val locationList: ArrayList<UserLocation> = ArrayList()
        firestoreDB.collection("location")
            .get()
            .addOnSuccessListener { result ->
                for (document in result ) {
                    locationCounter++
                    if (locationCounter <= 10){
                        locationList.add(
                            UserLocation(
                                latitude = document.data["latitude"].toString(),
                                longitude = document.data["longitude"].toString(),
                                date = document.data["date"].toString()
                            )
                        )
                    }else{
                        locationCounter = 0
                        currentLocation.value = locationList
                        return@addOnSuccessListener
                    }
                    Log.d("Firestore", "Success getting documents.")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents.", exception)
                throw exception
            }
        currentLocation.value = locationList
        Log.d("Firestore", "$locationList")
        return currentLocation
    }

}