package org.wit.treasuremap.models.persistence

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.wit.treasuremap.models.TreasureModel
import org.wit.treasuremap.util.exists
import org.wit.treasuremap.util.read
import org.wit.treasuremap.util.write

// fully AI generated due to time constraints
class TreasureJSONStore(private val context: Context) : TreasureStore {
    var treasures = mutableListOf<TreasureModel>()
    private val fileName = "treasures.json"
    private val gson = Gson()

    init {
        if (exists(context, fileName)) {
            val jsonString = read(context, fileName)
            val type = object : TypeToken<ArrayList<TreasureModel>>() {}.type
            treasures = gson.fromJson(jsonString, type)
        }
    }

    override fun findAll(): List<TreasureModel> = treasures

    override fun create(treasure: TreasureModel) {
        treasures.add(treasure)
        // Convert the list to text and save to disk
        write(context, fileName, gson.toJson(treasures))
    }

    private fun serialize() {
        val jsonString = gson.toJson(treasures)
        write(context, fileName, jsonString)
    }
}