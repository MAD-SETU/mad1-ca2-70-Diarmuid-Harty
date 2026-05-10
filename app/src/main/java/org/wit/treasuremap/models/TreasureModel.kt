package org.wit.treasuremap.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.database.IgnoreExtraProperties

// Pasted my old model into AI and asked it how to adapt it to work with firebase, and it produced this.
// it's the same data points, but it changed my id var, added the @ignore and @exclude
// have since found it in the documentation
// got rid of mapping as it's apparently not necessary

/**
 * Data class representing a Treasure entry in the system.
 * Annotated with @IgnoreExtraProperties for Firebase Realtime Database compatibility,
 * and @Parcelize for easy passing between Android Activities.
 */
@IgnoreExtraProperties // Tells Firebase to ignore any data it doesn't recognize when mapping to this class
@Parcelize // Enables the Parcelable implementation automatically via Kotlin Parcelize plugin
data class TreasureModel(
    var id: String = "", // Unique Firebase push key identifier
    var creatorId: String = "", // The UID of the user who "buried" this treasure
    var treasureName: String = "", // Name or title of the treasure
    var description: String = "", // Hints or details about the treasure
    var found: Boolean = false, // Status flag to check if someone has discovered it
    var lat: Double = 0.0, // Exact latitude coordinate
    var lng: Double = 0.0, // Exact longitude coordinate
    var searchLat: Double = 0.0, // Obfuscated latitude used as the center of the search circle
    var searchLng: Double = 0.0 // Obfuscated longitude used as the center of the search circle
) : Parcelable // Inherits from Parcelable to support serialization for Intents
