package org.wit.treasuremap.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

// Pasted my old model into ai and asked it how to adapt it to work with firebase and it produced this.
// its the same datapoints but it changed my id var, added the @ignore and @exclude

@IgnoreExtraProperties // Tells Firebase to ignore any data it doesn't recognize
data class TreasureModel(
    var id: String = "", // Changed to var and default empty for Firebase keys
    var creatorId: String = "",
    var treasureName: String = "",
    var description: String = "",
    var isFound: Boolean = false,
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var creatorEmail: String = "" // Useful for displaying "Hidden by: user@gmail.com"
) {
    // This allows Firebase to map the data even if the constructor is empty
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "creatorId" to creatorId,
            "treasureName" to treasureName,
            "description" to description,
            "isFound" to isFound,
            "lat" to lat,
            "lng" to lng,
            "creatorEmail" to creatorEmail
        )
    }
}
