package org.wit.treasuremap.util

import android.graphics.Color
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.treasuremap.models.TreasureModel
import kotlin.math.*
import kotlin.random.Random
import androidx.core.graphics.toColorInt

// fully AI generated file - I can't do basic math...
class TreasureHelper {

    // TREASURE HELPERS
    // Lots of AI generated code below, particularly after refactoring


    // AI generated function to find the distance to the nearest target
    // i tried to change this myself but the fact distanceBetween returns an array confused me
    fun getDistance(userLocation: LatLng, treasure: TreasureModel): Int {
        val results = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude, userLocation.longitude,
            treasure.lat, treasure.lng,
            results
        )
        return results[0].toInt()
    }

    // mostly AI generated
    fun renderTreasures(mMap: GoogleMap, treasures: List<TreasureModel>) {
        mMap.clear()
        treasures.forEach { treasure ->
            if (treasure.found) {
                // if Treasure is found: Show the Marker at the real spot, kill circle
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(treasure.lat, treasure.lng))
                        .title(treasure.treasureName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                )
                marker?.tag = treasure
            } else {
                // if trasure not found Show only the search radius
                val circle = mMap.addCircle(
                    CircleOptions()
                        .center(LatLng(treasure.searchLat, treasure.searchLng))
                        .radius(100.0)
                        .strokeColor("#08D9D6".toColorInt())
                        .fillColor(Color.argb(30, 8, 217, 214))
                        .strokeWidth(3f)
                        .clickable(true)
                )
                circle.tag = treasure
            }
        }
    }

}
