package org.wit.treasuremap.util

import android.graphics.Color
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.treasuremap.models.TreasureModel
import kotlin.math.*
import kotlin.random.Random

// fully AI generated file - I can't do basic math...
class TreasureHelper {

    // TREASURE HELPERS
    // almost entirely AI generated code below, particularly after refactoring
    // unfortunate but necessary due to time and this being much harder than I expected

    // AI generated function (and explanation) to set a random location within 100m of the target for a random search radius
    /** Takes a center point and a radius in meters,
     * and returns a new random coordinate somewhere inside that circle.
     * Credits: I used the Polar Coordinate distribution formula to ensure
     * the treasure doesn't just bunch up in the center.    */
    fun getRandomLocationInRadius(center: LatLng, radiusInMeters: Double): LatLng {
        // 1. Get a random distance (sqrt ensures even distribution across the area)
        val distance = radiusInMeters * sqrt(Random.nextDouble())

        // 2. Get a random angle (0 to 360 degrees in radians)
        val angle = Random.nextDouble() * 2 * PI

        // 3. Convert meters to Latitude (approx 111,320 meters per degree)
        val offsetLat = (distance * cos(angle)) / 111320.0

        // 4. Convert meters to Longitude (adjusts based on how far from the equator you are)
        val offsetLng = (distance * sin(angle)) / (111320.0 * cos(Math.toRadians(center.latitude)))

        return LatLng(center.latitude + offsetLat, center.longitude + offsetLng)
    }

    // AI generated function to find the distance to the nearest target
    fun getDistanceToClosest(userLocation: LatLng, treasures: List<TreasureModel>): Float {
        if (treasures.isEmpty()) return Float.MAX_VALUE

        var minDistance = Float.MAX_VALUE
        val results = FloatArray(1)

        treasures.forEach { treasure ->
            Location.distanceBetween(
                userLocation.latitude, userLocation.longitude,
                treasure.lat, treasure.lng,
                results
            )
            if (results[0] < minDistance) minDistance = results[0]
        }
        return minDistance
    }

    // AI generated
    // todo: make the offset persistant so it isnt re calculated every time
    fun renderTreasures(mMap: GoogleMap, treasures: List<TreasureModel>) {
        mMap.clear()
        treasures.forEach { treasure ->
            val realSpot = LatLng(treasure.lat, treasure.lng)
            val randomizedCenter = getRandomLocationInRadius(realSpot, 100.0)

            mMap.addCircle(
                CircleOptions()
                .center(randomizedCenter)
                .radius(100.0)
                .strokeColor(Color.parseColor("#08D9D6"))
                .fillColor(Color.argb(30, 8, 217, 214))
                .strokeWidth(3f))

            mMap.addMarker(
                MarkerOptions()
                .position(realSpot)
                .title(treasure.treasureName)
                .visible(false))
        }
    }

}
