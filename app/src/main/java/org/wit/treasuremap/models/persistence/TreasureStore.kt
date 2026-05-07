package org.wit.treasuremap.models.persistence

import org.wit.treasuremap.models.TreasureModel

interface TreasureStore {
    fun create(treasure: TreasureModel)

    fun update(treasure: TreasureModel)

    fun delete(treasure: TreasureModel)
    fun findAll(): List<TreasureModel>

    fun findById(id: String): TreasureModel?
}