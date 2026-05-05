package org.wit.treasuremap.models.persistence

import org.wit.treasuremap.models.TreasureModel

interface TreasureStore {
    fun findAll(): List<TreasureModel>
    fun create(treasure: TreasureModel)
}