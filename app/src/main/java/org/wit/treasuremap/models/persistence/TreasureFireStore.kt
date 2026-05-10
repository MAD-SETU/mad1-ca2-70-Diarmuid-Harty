package org.wit.treasuremap.models.persistence


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.wit.treasuremap.models.TreasureModel
import timber.log.Timber.i

// Consider this entire file AI generated at this point.

/**
 * Implementation of TreasureStore using Firebase Realtime Database.
 * This class handles real-time data synchronization and CRUD operations.
 */
class TreasureFireStore: TreasureStore {
    // Reference to the root of the Firebase Realtime Database
    private var db = FirebaseDatabase.getInstance("https://treasurehunt-fd5d8-default-rtdb.firebaseio.com/").reference
    // Local cache of treasures to provide immediate access for findAll() and filtering
    private var treasures = mutableListOf<TreasureModel>()

    // AI generated - trying to get map to render on data change
    // Callback to notify the UI when data arrives or changes
    var onDataChanged: (() -> Unit)? = null
        set(value) {
            field = value
            // If data is already here when the listener is attached, notify immediately
            // This ensures the UI reflects the current state if data loaded before the listener was set
            if (treasures.isNotEmpty()) {
                field?.invoke()
            }
        }

    // init function is AI generated
    init {
        // Update data in real time by attaching a listener to the "treasures" node
        db.child("treasures").addValueEventListener(object : ValueEventListener {
            /**
             * Called whenever data at the "treasures" path changes.
             * @param snapshot The current data at the location.
             */
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<TreasureModel>()
                // Iterate through all children in the "treasures" node
                for (postSnapshot in snapshot.children) {
                    try {
                        // Attempt to map the snapshot data to a TreasureModel object
                        val treasure = postSnapshot.getValue(TreasureModel::class.java)
                        if (treasure != null) {
                            // Assign the Firebase key as the model's ID
                            treasure.id = postSnapshot.key ?: ""
                            list.add(treasure)
                        }
                    } catch (e: Exception) {
                        i("Firebase mapping error: ${e.message}")
                    }
                }
                // Update the local cache and notify any UI listeners
                treasures = list
                i("Firebase Sync: ${treasures.size} treasures loaded")

                // Trigger UI update callback
                onDataChanged?.invoke()
            }

            /**
             * Called if the listener is cancelled (e.g., due to permission issues).
             */
            override fun onCancelled(error: DatabaseError) {
                i("Firebase Error: ${error.message}")
            }
        })
    }

    /**
     * Persists a new treasure to Firebase.
     */
    override fun create(treasure: TreasureModel) {
        // Generate a unique push key for the new treasure
        val key = db.child("treasures").push().key
        key?.let {
            treasure.id = it
            // Save the treasure object under the generated key
            db.child("treasures").child(it).setValue(treasure)
            i("Firebase: Treasure Created with ID $it")
        }
    }

    /**
     * Updates an existing treasure in Firebase.
     */
    override fun update(treasure: TreasureModel) {
        // Overwrites the data at the specific treasure ID path
        db.child("treasures").child(treasure.id).setValue(treasure)
    }

    /**
     * Deletes a treasure from Firebase.
     */
    override fun delete(treasure: TreasureModel) {
        // Removes the value at the specific treasure ID path
        db.child("treasures").child(treasure.id).removeValue()
    }

    /**
     * Returns the local cache of treasures.
     */
    override fun findAll(): List<TreasureModel> = treasures

    /**
     * Finds a treasure in the local cache by its ID.
     */
    override fun findById(id: String): TreasureModel? {
        return treasures.find { it.id == id }
    }

    /**
     * Filters the local cache to find treasures created by a specific user.
     */
    override fun findByUserId(id: String?): List<TreasureModel> {
        return treasures.filter { it.creatorId == id }
    }

}
