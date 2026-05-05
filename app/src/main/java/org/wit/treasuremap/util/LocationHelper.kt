package org.wit.treasuremap.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

class LocationHelper(private val activity: Activity) {

    // LOCATION HELPERS
    // almost entirely AI generated code below, particularly after refactoring
    // unfortunate but necessary due to time and this being much harder than I expected

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    private var locationCallback: LocationCallback? = null

    // AI generated
    fun getUserLocation(callback: (LatLng) -> Unit) {
        // Default fallback to Dungarvan if things go wrong
        val defaultLocation = LatLng(52.0861, -7.6153)

        if (checkLocationPermissions()) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        callback(LatLng(location.latitude, location.longitude))
                    } else {
                        callback(defaultLocation)
                    }
                }.addOnFailureListener {
                    callback(defaultLocation)
                }
            } catch (_: SecurityException) {
                callback(defaultLocation)
            }
        } else {
            callback(defaultLocation)
        }
    }

    // check if location services are active
    // AI generated code
    fun isLocationEnabled(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // helper function to check for location permission
    // AI generated code
    fun checkLocationPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    // helper function to request permission if missing
    // AI generated code
    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }

    fun startTracking(onLocationReceived: (LatLng) -> Unit) {
        val locationRequest = LocationRequest.create().apply {
            interval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    onLocationReceived(LatLng(it.latitude, it.longitude))
                }
            }
        }

        if (checkLocationPermissions()) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
        }
    }

    fun stopTracking() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }
}