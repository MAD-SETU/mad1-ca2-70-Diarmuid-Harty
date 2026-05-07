package org.wit.treasuremap.models

data class TreasureModel(
    val id: String = java.util.UUID.randomUUID().toString(), // assign unique id
    val creatorId: String = "", // id of the user that created the treasure

    var treasureName: String = "", // the name of treasure
    var description: String = "", // details about the treasures hiding spot
    var isFound: Boolean = false, // treasure has been found

    var lat: Double = 0.0, // latitude
    var lng: Double = 0.0 // longitude
    )
