package org.wit.treasuremap.models.persistence


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.wit.treasuremap.models.TreasureModel
import timber.log.Timber.i

// Consider this entire file AI generated at this point.

class TreasureFireStore: TreasureStore {
    private var db = FirebaseDatabase.getInstance("https://treasurehunt-fd5d8-default-rtdb.firebaseio.com/").reference
    private var treasures = mutableListOf<TreasureModel>()

    // AI generated - trying to get map to render on data change
    // Callback to notify the UI when data arrives or changes
    var onDataChanged: (() -> Unit)? = null
        set(value) {
            field = value
            // If data is already here when the listener is attached, notify immediately
            if (treasures.isNotEmpty()) {
                field?.invoke()
            }
        }

    // init function is AI generated
    init {
        // Update data in real time
        db.child("treasures").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<TreasureModel>()
                for (postSnapshot in snapshot.children) {
                    try {
                        val treasure = postSnapshot.getValue(TreasureModel::class.java)
                        if (treasure != null) {
                            treasure.id = postSnapshot.key ?: ""
                            list.add(treasure)
                        }
                    } catch (e: Exception) {
                        i("Firebase mapping error: ${e.message}")
                    }
                }
                treasures = list
                i("Firebase Sync: ${treasures.size} treasures loaded")

                // Trigger UI update
                onDataChanged?.invoke()
            }
            override fun onCancelled(error: DatabaseError) {
                i("Firebase Error: ${error.message}")
            }
        })
    }

    override fun create(treasure: TreasureModel) {
        // Generate unique key
        val key = db.child("treasures").push().key
        key?.let {
            treasure.id = it
            // Save using the key
            db.child("treasures").child(it).setValue(treasure)
            i("Firebase: Treasure Created with ID $it")
        }
    }

    override fun update(treasure: TreasureModel) {
        db.child("treasures").child(treasure.id).setValue(treasure)
    }

    override fun delete(treasure: TreasureModel) {
        db.child("treasures").child(treasure.id).removeValue()
    }

    override fun findAll(): List<TreasureModel> = treasures

    override fun findById(id: String): TreasureModel? {
        return treasures.find { it.id == id }
    }

    override fun findByUserId(id: String?): List<TreasureModel> {
        return treasures.filter { it.creatorId == id }
    }

}