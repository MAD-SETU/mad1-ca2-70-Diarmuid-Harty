package org.wit.treasuremap.models.persistence

import org.wit.treasuremap.models.TreasureModel

/**
 * Interface defining the expected behavior for any Treasure data persistence layer.
 * This abstraction allows the app to switch between In-Memory, File-based, or Cloud storage easily.
 */
interface TreasureStore {
    /**
     * Adds a new treasure to the data store.
     * @param treasure The model containing the new treasure's details.
     */
    fun create(treasure: TreasureModel)

    /**
     * Updates an existing treasure's information in the data store.
     * @param treasure The model with updated fields.
     */
    fun update(treasure: TreasureModel)

    /**
     * Removes a treasure from the data store.
     * @param treasure The model representing the treasure to be deleted.
     */
    fun delete(treasure: TreasureModel)

    /**
     * Retrieves all treasures currently stored.
     * @return A list of all TreasureModel objects.
     */
    fun findAll(): List<TreasureModel>

    /**
     * Searches for a treasure by its unique identifier.
     * @param id The string ID of the treasure.
     * @return The found TreasureModel, or null if it doesn't exist.
     */
    fun findById(id: String): TreasureModel?

    /**
     * Retrieves all treasures that belong to a specific user.
     * @param id The unique user ID of the creator.
     * @return A list of TreasureModel objects created by that user.
     */
    fun findByUserId(id: String?): List<TreasureModel>
}
