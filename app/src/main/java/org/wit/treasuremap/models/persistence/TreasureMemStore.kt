package org.wit.treasuremap.models.persistence

import org.wit.treasuremap.models.TreasureModel
import org.wit.treasuremap.models.persistence.TreasureStore

class TreasureMemStore : TreasureStore {
    val treasures = ArrayList<TreasureModel>()

    override fun findAll(): List<TreasureModel> {
        return treasures
    }

    override fun create(treasure: TreasureModel) {
        treasures.add(treasure)
    }
}