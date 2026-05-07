package org.wit.treasuremap.main

import android.app.Application
import org.wit.treasuremap.models.TreasureModel
import timber .log.Timber
import timber .log.Timber.i
import org.wit.treasuremap.models.persistence.TreasureStore

// todo:
class MainApp: Application() {

    lateinit var users: UserStore
    lateinit var treasures: TreasureStore

    var currentUser: UserModel? = null // variable for logged in user

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        users = UserJSONStore(applicationContext) // AI code, enables storage access
        treasures = TreasureJSONStore(applicationContext) // AI code (i replicated the above line)
        i("App Started")

        makeFakeTreasure()
    }

    fun makeFakeTreasure() {
        val currentTreasures = treasures.findAll()
        if (currentTreasures.isEmpty()) {
            i("No treasures found. Inserting example data...")
            treasures.create(
                TreasureModel(
                    treasureName = "Example",
                    description = "Hidden near me",
                    lat = 52.055598,
                    lng = -7.605826
                )
            )
        }
}
}
