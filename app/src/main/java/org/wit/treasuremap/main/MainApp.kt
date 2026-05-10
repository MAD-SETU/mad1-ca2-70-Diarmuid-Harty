package org.wit.treasuremap.main

import android.app.Application
import org.wit.treasuremap.models.persistence.TreasureFireStore
import org.wit.treasuremap.models.persistence.TreasureStore
import timber.log.Timber
import timber.log.Timber.i

/**
 * Main Application class that initializes global state and libraries.
 * This class is instantiated when the app process starts.
 */
class MainApp: Application() {

    // Global reference to the data persistence layer, accessible from any activity
    lateinit var treasures : TreasureStore

    override fun onCreate() {
        super.onCreate()
        // Initialize Timber logging library for debug builds
        Timber.plant(Timber.DebugTree())

        // Initialize the persistence layer using the Firebase implementation
        // This is done once at the app level so all activities share the same data source
        treasures = TreasureFireStore()

        i("App Started")
    }
}
