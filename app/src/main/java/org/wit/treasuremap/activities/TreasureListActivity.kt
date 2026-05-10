package org.wit.treasuremap.activities

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
import android.location.Location

class TreasureListActivity : AppCompatActivity() {
    lateinit var app: MainApp
    private lateinit var binding: ActivityTreasureListBinding
    // location stuff for distance to treasure calc
    private lateinit var locationHelper: LocationHelper

    // variables for helping with ui update
    private var lastLocation: LatLng? = null
    private var currentFilter = "ALL"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTreasureListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
        // link location helper
        locationHelper = LocationHelper(this)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        // the data come in relatively slowly so i need to force a few updates at different conditions
        // had AI generate this, i wouldnt have figure it out

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

        // 3. Update the UI
        binding.recyclerView.adapter = TreasureAdapter(filteredList, lastLocation)
    }
}
class TreasureAdapter(
    // treasures list
    private var treasures: List<TreasureModel>,
    // user lat long
    private val userLocation: LatLng?,
    private val listener: TreasureListener ) : // interface for treasure crud

    RecyclerView.Adapter<TreasureAdapter.MainHolder>() {

    interface TreasureListener {
        fun onTreasureClick(treasure: TreasureModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = TreasureDetailsCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        //val treasure = treasures[holder.adapterPosition]
        val treasure = treasures[holder.bindingAdapterPosition] // asked AI if the deprecation above is an issue, and it said no but this will fix the warning
        holder.bind(treasure, userLocation)
    }

    override fun getItemCount(): Int = treasures.size

    class MainHolder(private val binding: TreasureDetailsCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

            // treasure helper instance for use below
            private val helper = TreasureHelper()

        fun bind(treasure: TreasureModel, userLocation: LatLng?) {
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

        }
    }
}