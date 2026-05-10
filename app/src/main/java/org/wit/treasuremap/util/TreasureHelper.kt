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

/**
 * Utility class to handle treasure-specific calculations and map rendering.
 */
class TreasureHelper {

    // TREASURE HELPERS
    // Lots of AI generated code below, particularly after refactoring


    // AI generated function to find the distance to the nearest target
    // i tried to change this myself but the fact distanceBetween returns an array confused me
    /**
     * Calculates the distance in meters between a user and a specific treasure.
     * Uses Android's Location.distanceBetween utility.
     */
    fun getDistance(userLocation: LatLng, treasure: TreasureModel): Int {
        val results = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude, userLocation.longitude,
            treasure.lat, treasure.lng,
            results
        )
        // Returns the first (and only) value in the results array as an integer
        return results[0].toInt()
    }

    // mostly AI generated
    /**
     * Renders markers or search circles on the Google Map for a list of treasures.
     * @param mMap The GoogleMap instance to draw on.
     * @param treasures The list of treasure models to display.
     */
    fun renderTreasures(mMap: GoogleMap, treasures: List<TreasureModel>) {
        // Clear all existing markers/circles before re-drawing
        mMap.clear()
        
        treasures.forEach { treasure ->
            if (treasure.found) {
                // if Treasure is found: Show the Marker at the real spot, kill circle
                // Use a yellow marker to signify a "discovered" treasure
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(treasure.lat, treasure.lng))
                        .title(treasure.treasureName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                )
                // Attach the treasure object to the marker for later retrieval (e.g., on click)
                marker?.tag = treasure
            } else {
                // if treasure not found Show only the search radius
                // Draws a 100m radius circle around the obfuscated search center
                val circle = mMap.addCircle(
                    CircleOptions()
                        .center(LatLng(treasure.searchLat, treasure.searchLng))
                        .radius(100.0) // 100 meter search radius
                        .strokeColor("#08D9D6".toColorInt()) // Cyan stroke
                        .fillColor(Color.argb(30, 8, 217, 214)) // Transparent cyan fill
                        .strokeWidth(3f)
                        .clickable(true) // Allow the circle to be clicked in the UI
                )
                // Attach the treasure object to the circle for later retrieval
                circle.tag = treasure
            }
        }
    }

}
