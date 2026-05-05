package org.wit.treasuremap.models.persistence

import org.wit.treasuremap.models.UserModel
import org.wit.treasuremap.models.persistence.UserStore

class UserMemStore : UserStore {
    val users = ArrayList<UserModel>()

    override fun findAll(): List<UserModel> {
        return users
    }

    override fun create(user: UserModel) {
        users.add(user)
    }
}