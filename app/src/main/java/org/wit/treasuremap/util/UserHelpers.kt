package org.wit.treasuremap.util

import org.wit.treasuremap.models.UserModel
import org.wit.treasuremap.models.persistence.UserStore

// returns user object if name is found in array or null if not,
// null allows for knowing the user doesn't exist in array.
// I had AI check my logic here, and it suggested adding ignoreCase true,
// also switching (it.username ==) to (it.username.equals) to make that work
// "so that Bob and bob are not two different accounts"
fun getUser(input: String, store: UserStore): UserModel? {
    return store.findAll().find { it.username.equals(input, ignoreCase = true) }
}