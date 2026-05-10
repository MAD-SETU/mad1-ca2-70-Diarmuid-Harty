package org.wit.treasuremap.activities

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

class TreasureListActivity : AppCompatActivity(),
    TreasureAdapter.TreasureListener {
    lateinit var app: MainApp
    private lateinit var binding: ActivityTreasureListBinding

    // location stuff for distance to treasure calc
    private lateinit var locationHelper: LocationHelper

    // variables for helping with ui update
    private var lastLocation: LatLng? = null
    private var currentFilter = "ALL"
    private var selectedTreasure: TreasureModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTreasureListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
        // link location helper
        locationHelper = LocationHelper(this)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        binding.btnDelete.setOnClickListener {
            selectedTreasure?.let {
                app.treasures.delete(it)
                selectedTreasure = null // Clear selection after delete
                refreshUI()
                // Disable buttons again after deletion
                binding.btnDelete.isEnabled = false
                binding.btnUpdate.isEnabled = false
            }
            refreshUI()
        }

        binding.btnUpdate.setOnClickListener {
            selectedTreasure?.let {
                // Your intent to go to the Edit screen
                val intent = Intent(this, TreasuremapActivity::class.java)
                intent.putExtra("treasure_edit", it)
                startActivity(intent)
            }
        }

        // FIX: Add the filter listener so the buttons actually work
        binding.filterGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                currentFilter = when (checkedId) {
                    R.id.btnMine -> "MINE"
                    R.id.btnFound -> "DISCOVERED"
                    else -> "ALL"
                }
                refreshUI()
            }
        }

        // the data comes in relatively slowly so i need to force a few updates at different conditions
        // had AI generate this, I wouldn't have figured it out in time

        // Firebase trigger
        (app.treasures as TreasureFireStore).onDataChanged = {
            runOnUiThread { refreshUI() }
        }

        // Location Helper trigger
        locationHelper.getUserLocation { location ->
            lastLocation = location
            refreshUI() // This will update the "Locating..." labels
        }
    }

    private fun refreshUI() {
        val allTreasures = app.treasures.findAll()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // filtering logic
        var filteredList = when (currentFilter) {
            "MINE" -> allTreasures.filter { it.creatorId == userId }
            "DISCOVERED" -> allTreasures.filter { it.found == true }
            else -> allTreasures.filter { it.found == false }
        }

        // this is AI generated
        // i asked it what is the simples way to sort by distance with the current setup
        if (lastLocation != null) {
            filteredList = filteredList.sortedBy { treasure ->
                val treasureLoc = Location("").apply {
                    latitude = treasure.lat
                    longitude = treasure.lng
                }
                val userLoc = Location("").apply {
                    latitude = lastLocation!!.latitude
                    longitude = lastLocation!!.longitude
                }
                userLoc.distanceTo(treasureLoc) // This value determines the order
            }

        }

        // This is half AI generated and half practical exercise code
        binding.recyclerView.adapter = TreasureAdapter(filteredList, lastLocation, this)    }

    override fun onTreasureClick(treasure: TreasureModel) {
        // Hold the treasure in the variable we declared at the top
        selectedTreasure = treasure
        // Enable your bottom buttons
        binding.btnDelete.isEnabled = true
        binding.btnUpdate.isEnabled = true
    }

}

class TreasureAdapter(
    // treasures list
    private var treasures: List<TreasureModel>,
    // user lat long
    private val userLocation: LatLng?,
    private val listener: TreasureListener ) : // interface for treasure crud

    RecyclerView.Adapter<TreasureAdapter.MainHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    // for selecting a treasure in the list
    fun getSelectedTreasure(): TreasureModel? {
        return if (selectedPosition != RecyclerView.NO_POSITION) treasures[selectedPosition] else null
    }

    interface TreasureListener {
        fun onTreasureClick(treasure: TreasureModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = TreasureDetailsCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    // Fully Ai generated function. supports the selection of a list item, highlighting it
    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val treasure = treasures[holder.bindingAdapterPosition]

        // 1. UI HIGHLIGHTING
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#3300E5FF"))
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        // 2. BIND DATA (Pass the existing class listener)
        holder.bind(treasure, userLocation, listener)

        // 3. SELECTION LOGIC
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
    override fun getItemCount(): Int = treasures.size

    // 'inner' allows this class to access the 'listener' passed to the Adapter
    inner class MainHolder(private val binding: TreasureDetailsCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

            // treasure helper instance for use below
            private val helper = TreasureHelper()

        fun bind(treasure: TreasureModel, userLocation: LatLng?, listener: TreasureListener) {
            binding.treasureName.text = treasure.treasureName
            binding.description.text = treasure.description

            // distance calc for this treasure
            if (userLocation != null) { // if yes user location read
                val distance = helper.getDistance(userLocation, treasure)
                binding.distance.text = "${distance}M"
            } else {
                // The "No Location" fallback
                binding.distance.text = "Locating..."
            }

            // apply click listener for treasure item in menu
            binding.root.setOnClickListener { listener.onTreasureClick(treasure) }
        }
    }
}