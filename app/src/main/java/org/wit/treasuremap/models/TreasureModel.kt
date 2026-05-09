package org.wit.treasuremap.models

import com.google.firebase.database.IgnoreExtraProperties

// Pasted my old model into AI and asked it how to adapt it to work with firebase, and it produced this.
// it's the same data points, but it changed my id var, added the @ignore and @exclude
// have since found it in the documentation
// got rid of mapping as it's apparently not necessary

@IgnoreExtraProperties // Tells Firebase to ignore any data it doesn't recognize
data class TreasureModel(
    var id: String = "", // Changed to var and default empty for Firebase keys
    var creatorId: String = "", // unique id of the user who created the treasure
    var treasureName: String = "", // Treasures name
    var description: String = "", // treasure description
    var found: Boolean = false,
    var lat: Double = 0.0, // treasure latitude
    var lng: Double = 0.0, // treasure longitude
    var searchLat: Double = 0.0, // offset latitude for search radius
    var searchLng: Double = 0.0 // offset longitude for search radius
)
