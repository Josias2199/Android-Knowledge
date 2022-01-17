package com.joxs.knowledge.ui.locations

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.joxs.knowledge.databinding.FragmentLocationsBinding
import com.joxs.knowledge.R
import com.joxs.knowledge.utils.PermissionListener
import com.joxs.knowledge.utils.RequestPermissionDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.location.*
import com.joxs.knowledge.data.local.entity.UserLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.SupportMapFragment
import com.joxs.knowledge.data.network.Resource
import com.joxs.knowledge.presentation.LocationsViewModel
import com.joxs.knowledge.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val GPS_TIME_INTERVAL:Long = 1000 * 60 * 5
private const val LOCATION_PERMISSION_TAG = "cameraPermission"
private const val PERMISSION_DENIED_TAG = "deniedPermission"

@AndroidEntryPoint
class LocationsFragment : Fragment(), PermissionListener {

    private val viewModel: LocationsViewModel by viewModels()
    private lateinit var binding: FragmentLocationsBinding
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        FirebaseApp.initializeApp(requireContext())
        binding = FragmentLocationsBinding.inflate(layoutInflater)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mapFragment = this.childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        createLocationRequest()
        locationCallback()
        checkLocationPermission()
        setupPermissionLauncher()

        return binding.root
    }

    private fun setupPermissionLauncher(){
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    locationCallback()
                    getCurrentLocations()
                } else {
                    requestLocationPermissionDialog(
                        image = R.drawable.placeholder_error,
                        description = getString(R.string.msg_request_location_permission_denied),
                        TAG = PERMISSION_DENIED_TAG
                    )

                }
            }
    }

    private fun checkLocationPermission(){
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                locationCallback()
                getCurrentLocations()
            }
            shouldShowRequestPermissionRationale(locationPermission) -> {
                requestLocationPermissionDialog(
                    image = R.drawable.placeholder_google_maps,
                    description = getString(R.string.msg_request_location_permission),
                    TAG = LOCATION_PERMISSION_TAG
                )
            }
            else -> {
                requestLocationPermissionDialog(
                    image = R.drawable.placeholder_google_maps,
                    description = getString(R.string.msg_request_location_permission),
                    TAG = LOCATION_PERMISSION_TAG
                )
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = GPS_TIME_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun getCurrentLocations(){
        viewModel.getCurrentLocation().observe(viewLifecycleOwner, { userLocations ->
            if(userLocations.isNotEmpty()){
                mapFragment.getMapAsync { googleMap ->
                    for (location in userLocations) {
                        val currentLocation =
                            LatLng(location.latitude.toDouble(), location.longitude.toDouble())
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(currentLocation)
                                .title(location.date)
                        )
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                    }
                    googleMap.moveCamera(CameraUpdateFactory.zoomIn())
                }
            }
        })
    }

    private fun locationCallback(){
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.let {
                    for (location in locationResult.locations) {
                        location?.let {
                            val latitude = it.latitude.toString()
                            val longitude = it.longitude.toString()
                            val date = Calendar.getInstance().time
                            val currentLocation = UserLocation(latitude, longitude, date.toString())
                            viewModel.saveCurrentLocation(currentLocation)
                                .observe(viewLifecycleOwner, { resource ->
                                    saveCurrentLocationResponse(resource)
                            })
                        }
                    }
                }
            }
        }
    }

    private fun saveCurrentLocationResponse(resource: Resource<out Unit>){
        when(resource){
            is Resource.Success -> {createNotification()}
            is Resource.Error -> { showToast(getString(R.string.msg_location_could_not_be_saved))}
            else -> {}
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.notification_channel_id), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() {
        Log.d("Firestore", "Crear notificación")
        val builder = NotificationCompat.Builder(
            requireActivity(),
            getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_location)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        builder.build()
        createNotificationChannel()
        with(NotificationManagerCompat.from(requireActivity())) {
            notify(12358, builder.build())
        }

    }

    private fun requestLocationPermissionDialog(
        image: Int,
        description: String,
        TAG: String
    ){
        val dialog = RequestPermissionDialog.newInstance(image, description)
        dialog.setListener(this)
        dialog.isCancelable = false
        dialog.show(requireActivity().supportFragmentManager, TAG)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        if (dialog.tag == LOCATION_PERMISSION_TAG)
            requestPermissionLauncher.launch(locationPermission)
    }

   override fun onResume() {
        super.onResume()
        locationCallback?.let {
            getCurrentLocations()
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let {
            stopLocationUpdates()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback!!)
    }


}