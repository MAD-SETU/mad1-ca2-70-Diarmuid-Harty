package org.wit.treasuremap.models.persistence

import com.google.firebase.database.FirebaseDatabase
import org.wit.treasuremap.models.TreasureModel

class TreasureFireStore: TreasureStore {
    private var db = FirebaseDatabase.getInstance().reference
    private var treasures = mutableListOf<TreasureModel>()

    override fun create(treasure: TreasureModel) {
        // TODO: Implement Cloud create
    }

    override fun update(treasure: TreasureModel) {
        // TODO: Implement Cloud Update
    }

    override fun delete(treasure: TreasureModel) {
        // TODO: Implement Cloud Delete
    }

    override fun findAll(): List<TreasureModel> = treasures

    override fun findById(id: String): TreasureModel? {
        return treasures.find { it.id == id }
    }

}