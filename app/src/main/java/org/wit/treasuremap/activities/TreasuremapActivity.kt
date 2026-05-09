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
import org.wit.treasuremap.util.updateLightBar
import timber.log.Timber.i

class TreasuremapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityTreasuremapBinding
    private lateinit var app: MainApp
    private lateinit var mMap: GoogleMap

    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTreasuremapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MainApp

        // check user is logged in
        checkLoggedIn()
        setupMapFragment()
        startNavMenu()
        startTreasureCard()
        startProfileCard()

        // Initialize Helper and start the proximity loop
        locationHelper = LocationHelper(this)
        locationHelper.startTracking { latLng -> updateProximityUI(latLng) }

        // This makes the ui update on data change
        // AI generated
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

    // USER INTERFACE
    // had AI walk me through this at the start (startNav)

    // NAVIGATION UI
    private fun startNavMenu() {
        with(binding.controlLayout) {
            btnMenuToggle.setOnClickListener {
                binding.toggleMenu()
            }

            btnToggleAdd.setOnClickListener {
                if (locationHelper.isLocationEnabled()) {
                    binding.addTreasureLayout.root.toggle()
                    binding.controlLayout.root.toggle()
                }
            }

            btnProfile.setOnClickListener {
                binding.profileLayout.root.toggle()
            }
        }
    }

    // TREASURE CREATION
    private fun startTreasureCard() {
        with(binding.addTreasureLayout) {
            btnCancelTreasure.setOnClickListener {
                binding.resetAddCard()
            }

            btnAddTreasure.setOnClickListener {
                val name = treasureNameField.text.toString()

                val latOffset = (Math.random() - 0.5) * 0.001
                val lngOffset = (Math.random() - 0.5) * 0.001

                if (name.isEmpty()) {
                    Snackbar.make(binding.root, "Enter a name", Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                locationHelper.getUserLocation { coords ->
                    val treasure = TreasureModel(
                        treasureName = name,
                        creatorId = FirebaseAuth.getInstance().currentUser?.uid ?: "", // elvis operator used as currentUser is nullable
                        description = treasureDescriptionField.text.toString(),
                        lat = coords.latitude,
                        lng = coords.longitude,
                        searchLat = coords.latitude + latOffset,
                        searchLng = coords.longitude + lngOffset
                    )
                    app.treasures.create(treasure)

                    binding.resetAddCard()
                    Snackbar.make(binding.root, "Treasure Buried!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    // USER PROFILE
    //AI refactored
    private fun startProfileCard() {

        val user = FirebaseAuth.getInstance().currentUser

        // todo: get card data

        binding.profileLayout.root.isVisible = false

        binding.profileLayout.profileCard.setOnClickListener {
            binding.profileLayout.root.isVisible = false
        }
    }

    private fun checkLoggedIn() {

        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) { // if no user, run login
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Prevents going back to map screen on back press
            return // Prevents further execution if user is not logged in
        }
    }



    // AI generated
    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager // find the SupportMapFragment
            .findFragmentById(R.id.map) as SupportMapFragment // cast it to SupportMapFragment
        mapFragment.getMapAsync(this) // tell it to run onMapReady()
    }

    // AI generated, refactored to use helpers
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        val irelandCenter = LatLng(53.1424, -7.6921)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(irelandCenter, 6.7f))

        if (locationHelper.checkLocationPermissions()) {
            try { mMap.isMyLocationEnabled = true } catch (_: SecurityException) {}
        } else {
            locationHelper.requestLocationPermissions()
        }

        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = false

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ zoomToUser() }, 2000)
        TreasureHelper().renderTreasures(mMap, app.treasures.findAll())
    }

    private fun zoomToUser() {
        locationHelper.getUserLocation { coords ->
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 15f), 2000, null)
        }
    }

    /// AI generated & Refactored to use TreasureHelper
    private fun updateProximityUI(userLocation: LatLng) {
        val treasures = app.treasures.findAll()
        val distance = TreasureHelper().getDistanceToClosest(userLocation, treasures)

        val lights: List<android.view.View> = listOf(
            binding.proxBarLayout.light1, binding.proxBarLayout.light2,
            binding.proxBarLayout.light3, binding.proxBarLayout.light4,
            binding.proxBarLayout.light5, binding.proxBarLayout.light6
        )
        lights.updateLightBar(distance)
    }

    // AI generated, stops tracking on close
    override fun onDestroy() {
        super.onDestroy()
        locationHelper.stopTracking() // Stops the GPS when the app closes
    }

}