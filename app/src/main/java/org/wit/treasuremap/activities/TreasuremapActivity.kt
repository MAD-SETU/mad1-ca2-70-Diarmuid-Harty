package org.wit.treasuremap.activities

//import org.wit.treasuremap.util.renderProfileData
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.wit.treasuremap.R
import org.wit.treasuremap.databinding.ActivityTreasuremapBinding
import org.wit.treasuremap.main.MainApp
import org.wit.treasuremap.models.TreasureModel
import org.wit.treasuremap.models.persistence.TreasureFireStore
import org.wit.treasuremap.util.LocationHelper
import org.wit.treasuremap.util.TreasureHelper
import org.wit.treasuremap.util.resetAddCard
import org.wit.treasuremap.util.toggle
import org.wit.treasuremap.util.toggleMenu
//import org.wit.treasuremap.util.updateLightBar
import timber.log.Timber.i

/**
 * Main Activity that displays the Google Map and handles treasure placement.
 * Implements OnMapReadyCallback to receive notification when the map is ready to be used.
 */
class TreasuremapActivity : AppCompatActivity(), OnMapReadyCallback {
    // View Binding for activity_treasuremap layout
    private lateinit var binding: ActivityTreasuremapBinding
    // Reference to the main application class for shared data access
    private lateinit var app: MainApp
    // Google Map instance
    private lateinit var mMap: GoogleMap

    // Helper for managing location-related tasks (permissions, current coords)
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize View Binding
        binding = ActivityTreasuremapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Get application context cast to MainApp
        app = application as MainApp

        // check user is logged in before proceeding
        checkLoggedIn()
        // Initialize the SupportMapFragment
        setupMapFragment()
        // Initialize UI components and their listeners
        startNavMenu()
        startTreasureCard()
        startProfileCard()

        // Initialize Helper and start the proximity loop
        locationHelper = LocationHelper(this)
        //locationHelper.startTracking { latLng -> updateProximityUI(latLng) }

        // This makes the ui update on data change
        // AI generated
        // Setup listener for Firebase data changes to refresh markers on the map
        (app.treasures as TreasureFireStore).onDataChanged = {
            i("Firebase data changed, refreshing map...")
            runOnUiThread { // only thread that can touch ui on android
                if (::mMap.isInitialized) {
                    // call the rendering logic when data arrives
                    TreasureHelper().renderTreasures(mMap, app.treasures.findAll())
                }
            }
        }

    }

    // USER INTERFACE ELEMENTS


    // NAVIGATION UI
    /**
     * Sets up click listeners for the main navigation controls.
     */
    private fun startNavMenu() {
        with(binding.controlLayout) {
            // Button to open/close the side menu
            btnMenuToggle.setOnClickListener {
                binding.toggleMenu()
            }

            // treasure list view
            // Navigates to the TreasureListActivity
            btnListView.setOnClickListener {
                binding.toggleMenu() // Close menu before switching
                val intent = Intent(this@TreasuremapActivity, TreasureListActivity::class.java)
                startActivity(intent)
            }

            // Toggles the "Add Treasure" layout visibility
            btnToggleAdd.setOnClickListener {
                if (locationHelper.isLocationEnabled()) {
                    binding.addTreasureLayout.root.toggle()
                    binding.controlLayout.root.toggle()
                }
            }

            // Toggles the user profile card visibility
            btnProfile.setOnClickListener {
                binding.profileLayout.root.toggle()
            }
        }
    }

    // TREASURE CREATION
    /**
     * Sets up listeners for the treasure creation card.
     */
    private fun startTreasureCard() {
        with(binding.addTreasureLayout) {
            // Closes the add card without saving
            btnCancelTreasure.setOnClickListener {
                binding.resetAddCard()
            }

            // Handles creating and saving a new treasure
            btnAddTreasure.setOnClickListener {
                val name = treasureNameField.text.toString()

                // Calculate a small random offset for the search center to make finding harder
                val latOffset = (Math.random() - 0.5) * 0.001
                val lngOffset = (Math.random() - 0.5) * 0.001

                // Validation: name cannot be empty
                if (name.isEmpty()) {
                    Snackbar.make(binding.root, "Enter a name", Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // Get current location and then create the treasure entry
                locationHelper.getUserLocation { coords ->
                    val treasure = TreasureModel(
                        treasureName = name,
                        creatorId = FirebaseAuth.getInstance().currentUser?.uid ?: "", // elvis operator used as currentUser is nullable
                        description = treasureDescriptionField.text.toString(),
                        lat = coords.latitude, // Exact location
                        lng = coords.longitude,
                        searchLat = coords.latitude + latOffset, // Obfuscated search center
                        searchLng = coords.longitude + lngOffset
                    )
                    // Persist to Firebase
                    app.treasures.create(treasure)

                    // Clear the UI and show confirmation
                    binding.resetAddCard()
                    Snackbar.make(binding.root, "Treasure Buried!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    // USER PROFILE
    //AI refactored
    /**
     * Sets up the user profile display.
     */
    private fun startProfileCard() {

        val user = FirebaseAuth.getInstance().currentUser

        // todo: get card data

        // Initially hide the profile card
        binding.profileLayout.root.isVisible = false

        // Close profile when clicking on the card background
        binding.profileLayout.profileCard.setOnClickListener {
            binding.profileLayout.root.isVisible = false
        }
    }



    // FUNCTIONS

    // delete all the treasure made by the user
    /**
     * Utility function to remove all treasures created by the currently logged-in user.
     */
    private fun deleteAllUserTreasure() {
        // get user
        val userId = getUserId()
        // define a list of the treasures under the above user id
        val userTreasures = app.treasures.findByUserId(userId)

        // Iterate and delete each
        userTreasures.forEach { treasure ->
            app.treasures.delete(treasure)}
    }

    // get current users id
    /**
     * Returns the UID of the currently authenticated Firebase user.
     */
    private fun getUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    /**
     * Checks if a user session exists; if not, redirects to LoginActivity.
     */
    private fun checkLoggedIn() {

        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) { // if no user, run login
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Prevents going back to map screen on back press
            return // Prevents further execution if user is not logged in
        }
    }

    // AI generated
    /**
     * Finds the map fragment and triggers the async loading process.
     */
    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager // find the SupportMapFragment
            .findFragmentById(R.id.map) as SupportMapFragment // cast it to SupportMapFragment
        mapFragment.getMapAsync(this) // tell it to run onMapReady() when initialization completes
    }

    // AI generated, refactored to use helpers
    /**
     * Callback from Google Maps when initialization is complete.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Apply custom map style from resources
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        // Initial camera position centered on Ireland
        val irelandCenter = LatLng(53.1424, -7.6921)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(irelandCenter, 6.7f))

        // Enable "My Location" layer if permissions are granted
        if (locationHelper.checkLocationPermissions()) {
            try { mMap.isMyLocationEnabled = true } catch (_: SecurityException) {}
        } else {
            locationHelper.requestLocationPermissions()
        }

        // Configure UI settings
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = false

        // Delay zoom to user to ensure location has been acquired
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ zoomToUser() }, 2000)
        // Initial render of existing treasures
        TreasureHelper().renderTreasures(mMap, app.treasures.findAll())
    }

    /**
     * Animates the camera to the user's current location.
     */
    private fun zoomToUser() {
        locationHelper.getUserLocation { coords ->
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 15f), 2000, null)
        }
    }
    
    // AI generated, stops tracking on close
    /**
     * Standard lifecycle cleanup.
     */
    override fun onDestroy() {
        super.onDestroy()
        locationHelper.stopTracking() // Stops the GPS when the app closes to save battery
    }
}
