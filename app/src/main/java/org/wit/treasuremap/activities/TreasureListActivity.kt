package org.wit.treasuremap.activities

// Required Android and Google Play Services imports for UI, Maps, and Location
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
// View Binding and Project specific imports
import org.wit.treasuremap.databinding.ActivityTreasureListBinding
import org.wit.treasuremap.databinding.TreasureDetailsCardBinding
import org.wit.treasuremap.main.MainApp
import org.wit.treasuremap.models.TreasureModel
import org.wit.treasuremap.models.persistence.TreasureFireStore
import org.wit.treasuremap.util.LocationHelper
import org.wit.treasuremap.util.TreasureHelper
import android.content.Intent
import org.wit.treasuremap.R
import timber.log.Timber.i
import kotlin.collections.get

/**
 * Main activity for displaying the list of treasures.
 * Implements TreasureListener to handle clicks on individual treasure items.
 */
class TreasureListActivity : AppCompatActivity(),
    TreasureAdapter.TreasureListener {
    // Reference to the main application object for global state access
    lateinit var app: MainApp
    // Binding object for the activity layout
    private lateinit var binding: ActivityTreasureListBinding

    // location stuff for distance to treasure calc
    // Helper class to manage fetching the user's current GPS coordinates
    private lateinit var locationHelper: LocationHelper

    // variables for helping with ui update
    // Stores the last known user location to avoid unnecessary re-fetches
    private var lastLocation: LatLng? = null
    // Current filter state (ALL, MINE, or DISCOVERED)
    private var currentFilter = "ALL"
    // Reference to the treasure currently selected in the list
    private var selectedTreasure: TreasureModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize view binding and set the content view
        binding = ActivityTreasureListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize global app reference
        app = application as MainApp
        // link location helper
        // Create an instance of the location helper tied to this activity's lifecycle
        locationHelper = LocationHelper(this)

        // Set up the RecyclerView with a standard linear vertical layout
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        // Setup click listener for the Delete button
        binding.btnDelete.setOnClickListener {
            selectedTreasure?.let {
                // Remove from persistence layer (Firebase)
                app.treasures.delete(it)
                selectedTreasure = null // Clear selection after delete
                refreshUI() // Update list to reflect deletion
                // Disable buttons again after deletion since nothing is selected
                binding.btnDelete.isEnabled = false
                binding.btnUpdate.isEnabled = false
            }
            refreshUI()
        }

        // Setup click listener for the Update/Edit button
        binding.btnUpdate.setOnClickListener {
            selectedTreasure?.let {
                // Your intent to go to the Edit screen
                // Start the map activity in "edit mode" by passing the treasure object
                val intent = Intent(this, TreasuremapActivity::class.java)
                intent.putExtra("treasure_edit", it)
                startActivity(intent)
            }
        }

        // FIX: Add the filter listener so the buttons actually work
        // Listens for changes in the Material Button Group for filtering the list
        binding.filterGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                // Update the current filter based on which button was clicked
                currentFilter = when (checkedId) {
                    R.id.btnMine -> "MINE"
                    R.id.btnFound -> "DISCOVERED"
                    else -> "ALL"
                }
                refreshUI() // Refresh the list with the new filter applied
            }
        }

        // the data comes in relatively slowly so i need to force a few updates at different conditions
        // had AI generate this, I wouldn't have figured it out in time

        // Firebase trigger
        // Set up a callback for when data changes in the Firestore database
        (app.treasures as TreasureFireStore).onDataChanged = {
            // Ensure UI updates happen on the main thread
            runOnUiThread { refreshUI() }
        }

        // Location Helper trigger
        // Request the user's current location once to start distance calculations
        locationHelper.getUserLocation { location ->
            lastLocation = location
            refreshUI() // This will update the "Locating..." labels and sort by distance
        }
    }

    /**
     * Fetches the latest data, applies filters and sorting, and updates the RecyclerView adapter.
     */
    private fun refreshUI() {
        // Get all treasures from the store
        val allTreasures = app.treasures.findAll()
        // Get current user ID for the "Mine" filter
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // filtering logic
        // Create a sub-list based on the active filter state
        var filteredList = when (currentFilter) {
            "MINE" -> allTreasures.filter { it.creatorId == userId }
            "DISCOVERED" -> allTreasures.filter { it.found == true }
            else -> allTreasures.filter { it.found == false }
        }

        // this is AI generated
        // i asked it what is the simples way to sort by distance with the current setup
        // If we have a user location, sort the list so the closest treasures appear first
        if (lastLocation != null) {
            filteredList = filteredList.sortedBy { treasure ->
                // Create location objects for comparison
                val treasureLoc = Location("").apply {
                    latitude = treasure.lat
                    longitude = treasure.lng
                }
                val userLoc = Location("").apply {
                    latitude = lastLocation!!.latitude
                    longitude = lastLocation!!.longitude
                }
                // Use built-in distance calculation
                userLoc.distanceTo(treasureLoc) // This value determines the order
            }

        }

        // This is half AI generated and half practical exercise code
        // Create and attach a new adapter with the processed list
        binding.recyclerView.adapter = TreasureAdapter(filteredList, lastLocation, this)    }

    /**
     * Callback from the adapter when a treasure item is clicked.
     */
    override fun onTreasureClick(treasure: TreasureModel) {
        // Hold the treasure in the variable we declared at the top
        selectedTreasure = treasure
        // Enable your bottom buttons so the user can perform actions on this treasure
        binding.btnDelete.isEnabled = true
        binding.btnUpdate.isEnabled = true
    }

}

/**
 * Adapter class for managing the display of treasure items in a list.
 */
class TreasureAdapter(
    // treasures list
    private var treasures: List<TreasureModel>,
    // user lat long
    private val userLocation: LatLng?,
    private val listener: TreasureListener ) : // interface for treasure crud

    RecyclerView.Adapter<TreasureAdapter.MainHolder>() {

    // Tracks which item is currently selected for highlighting
    private var selectedPosition = RecyclerView.NO_POSITION

    // for selecting a treasure in the list
    // Returns the treasure object at the currently selected position
    fun getSelectedTreasure(): TreasureModel? {
        return if (selectedPosition != RecyclerView.NO_POSITION) treasures[selectedPosition] else null
    }

    /**
     * Interface for communicating clicks back to the activity.
     */
    interface TreasureListener {
        fun onTreasureClick(treasure: TreasureModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        // Inflate the layout for a single list item card
        val binding = TreasureDetailsCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    // Fully Ai generated function. supports the selection of a list item, highlighting it
    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        // Get the specific treasure for this list position
        val treasure = treasures[holder.bindingAdapterPosition]

        // 1. UI HIGHLIGHTING
        // Apply a background color if this item is the one the user clicked on
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#3300E5FF"))
        } else {
            // Remove highlighting for non-selected items
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        // 2. BIND DATA (Pass the existing class listener)
        // Fill the card with treasure data
        holder.bind(treasure, userLocation, listener)

        // 3. SELECTION LOGIC
        // Update selection state when an item is clicked
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.bindingAdapterPosition

            // Refresh only the items that changed to save performance
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            // Notify the Activity so it can enable the Update/Delete buttons
            listener.onTreasureClick(treasure)
        }
    }

    // from class material
    // Tells the RecyclerView how many items are in the list
    override fun getItemCount(): Int = treasures.size

    // 'inner' allows this class to access the 'listener' passed to the Adapter
    /**
     * ViewHolder class that holds the references to the views for a single list item.
     */
    inner class MainHolder(private val binding: TreasureDetailsCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

            // treasure helper instance for use below
            private val helper = TreasureHelper()

        /**
         * Populates the views with data from the TreasureModel.
         */
        fun bind(treasure: TreasureModel, userLocation: LatLng?, listener: TreasureListener) {
            binding.treasureName.text = treasure.treasureName
            binding.description.text = treasure.description

            // distance calc for this treasure
            // If the user's location is known, calculate the distance to this treasure
            if (userLocation != null) { // if yes user location read
                val distance = helper.getDistance(userLocation, treasure)
                binding.distance.text = "${distance}M"
            } else {
                // The "No Location" fallback displayed while waiting for GPS
                binding.distance.text = "Locating..."
            }

            // apply click listener for treasure item in menu
            binding.root.setOnClickListener { listener.onTreasureClick(treasure) }
        }
    }
}
