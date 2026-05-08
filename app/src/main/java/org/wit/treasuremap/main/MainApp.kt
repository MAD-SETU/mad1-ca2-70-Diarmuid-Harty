package org.wit.treasuremap.main

import android.app.Application
import org.wit.treasuremap.models.persistence.TreasureFireStore
import org.wit.treasuremap.models.persistence.TreasureStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp: Application() {

    lateinit var treasures : TreasureStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        // initialise firebase
        treasures = TreasureFireStore()

        i("App Started")
    }
}
