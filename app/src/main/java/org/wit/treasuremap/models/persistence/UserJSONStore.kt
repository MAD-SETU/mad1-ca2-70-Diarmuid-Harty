package org.wit.treasuremap.models.persistence

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.wit.treasuremap.models.UserModel
import org.wit.treasuremap.models.persistence.UserStore
import org.wit.treasuremap.util.exists
import org.wit.treasuremap.util.read
import org.wit.treasuremap.util.write
// fully AI generated due to time constraints
class UserJSONStore(private val context: Context) : UserStore {
    var users = mutableListOf<UserModel>()
    private val fileName = "users.json"
    private val gson = Gson()

    init {
        if (exists(context, fileName)) {
            val jsonString = read(context, fileName)
            val type = object : TypeToken<ArrayList<UserModel>>() {}.type
            users = gson.fromJson(jsonString, type)
        }
    }

    // overrides the interface method in userStore
    override fun findAll(): List<UserModel> = users

    // overrides the interface method in userStore
    override fun create(user: UserModel) {
        users.add(user)
        val jsonString = gson.toJson(users)
        write(context, fileName, jsonString)
    }
}