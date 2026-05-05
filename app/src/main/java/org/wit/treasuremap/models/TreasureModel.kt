package org.wit.treasuremap.models

data class TreasureModel(
    val id: String = java.util.UUID.randomUUID().toString(),
    var treasureName: String = "",
    var description: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var isFound: Boolean = false
    )
