package org.wit.treasuremap.models.persistence

import org.wit.treasuremap.models.UserModel

interface UserStore {
    fun findAll(): List<UserModel>
    fun create(user: UserModel)
}