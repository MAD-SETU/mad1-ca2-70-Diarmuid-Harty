package org.wit.treasuremap.models

data class UserModel(
    // asked AI easiest way to do unique ID, it gave me that uuid line
    val id: String = java.util.UUID.randomUUID().toString(),
    var avatar: String = "",
    var username: String = "",
    var treasureFound: Int = 0,
    var treasureCreated: Int = 0
)