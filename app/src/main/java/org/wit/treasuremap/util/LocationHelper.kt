package org.wit.treasuremap.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

/**
 * Helper class to manage location services, permissions, and updates.
 */
class LocationHelper(private val activity: Activity) {

    // LOCATION HELPERS
    // almost entirely AI generated code below, particularly after refactoring

    // FusedLocationProviderClient is the Google Play services location API
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    // Callback to handle incoming location updates
    private var locationCallback: LocationCallback? = null

    // AI generated
    /**
     * Attempts to get the user's last known location.
     * @param callback Function to execute with the resulting LatLng coordinates.
     */
    fun getUserLocation(callback: (LatLng) -> Unit) {
        // Default fallback to Dungarvan if things go wrong
        val defaultLocation = LatLng(52.0861, -7.6153)

        // Only proceed if the user has granted location permissions
        if (checkLocationPermissions()) {
            try {
                // Request the last known location from the provider
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        // Success: return actual coordinates
                        callback(LatLng(location.latitude, location.longitude))
                    } else {
                        // Success but location was null: return fallback
                        callback(defaultLocation)
                    }
                }.addOnFailureListener {
                    // API call failed: return fallback
                    callback(defaultLocation)
                }
            } catch (_: SecurityException) {
                // Should be caught by checkLocationPermissions, but here for safety
                callback(defaultLocation)
            }
        } else {
            // Permissions missing: return fallback
            callback(defaultLocation)
        }
    }

    // check if location services are active
    // AI generated code
    /**
     * Checks if GPS or Network location providers are enabled on the device.
     */
    fun isLocationEnabled(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // helper function to check for location permission
    // AI generated code
    /**
     * Checks if the ACCESS_FINE_LOCATION permission has been granted by the user.
     */
    fun checkLocationPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    // helper function to request permission if missing
    // AI generated code
    /**
     * Triggers the standard Android permission request dialog for location.
     */
    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }

    // AI generated code
    /**
     * Starts requesting periodic location updates (every 2 seconds).
     * @param onLocationReceived Callback triggered each time a new location is recorded.
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startTracking(onLocationReceived: (LatLng) -> Unit) {
        // Configure the update interval and accuracy priority
        val locationRequest = LocationRequest.create().apply {
            interval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        // Define what happens when a new location result is available
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    onLocationReceived(LatLng(it.latitude, it.longitude))
                }
            }
        }

        // Request updates from the fused location provider
        if (checkLocationPermissions()) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
        }
    }

    // AI generated code
    /**
     * Stops receiving periodic location updates to save battery.
     */
    fun stopTracking() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }
}
