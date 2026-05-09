package org.wit.treasuremap.util

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import org.wit.treasuremap.R
import org.wit.treasuremap.databinding.ActivityTreasuremapBinding
import org.wit.treasuremap.databinding.UserProfileCardBinding

// USER INTERFACE HELPERS
// almost entirely AI generated code below, particularly after refactoring
// unfortunate but necessary due to time and this being much harder than I expected

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

// quick Snackbar
fun Activity.showSnackbar(message: String) {
    Snackbar.make(this.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
}

// toggle Visibility
fun View.toggle() {
    this.visibility = if (this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

//fun List<View>.updateLightBar(distance: Float) {
//    val lightsToLight = when {
//        distance < 5 -> 6   // < 5m  = 6 lights
//        distance < 20 -> 5  // < 20m  = 5 lights
//        distance < 40 -> 4  // < 50m  = 4 lights
//        distance < 60 -> 3  // < 100m = 3 lights
//        distance < 80 -> 2  // < 150m = 2 lights
//        distance < 100 -> 1 // < 200m = 1 light
//        else -> 0           // > 100m = 0 lights
//    }

//    this.forEachIndexed { index, view ->
//        if (index < lightsToLight) {
//            view.setBackgroundColor(Color.parseColor("#08D9D6")) // cyan for lit
//            view.alpha = 1.0f
//        } else {
//            view.setBackgroundColor(Color.DKGRAY)
//            view.alpha = 0.3f
//        }
//    }
//}

    // Handle the Nav Menu Toggle icon and visibility
    fun ActivityTreasuremapBinding.toggleMenu() {
        controlLayout.expandableMenu.isVisible = !controlLayout.expandableMenu.isVisible
        val icon = if (controlLayout.expandableMenu.isVisible)
            android.R.drawable.ic_menu_close_clear_cancel else android.R.drawable.ic_menu_add
        controlLayout.btnMenuToggle.setImageResource(icon)
    }

    // Reset the Add Treasure UI back to normal
    fun ActivityTreasuremapBinding.resetAddCard() {
        addTreasureLayout.root.isVisible = false
        controlLayout.root.isVisible = true
        addTreasureLayout.treasureNameField.text.clear()
        addTreasureLayout.treasureDescriptionField.text.clear()
    }


//    fun renderProfileData(binding: UserProfileCardBinding, user: UserModel?) {
//        user?.let {
//            binding.profileUsername.text = it.username
//            // Show first 8 characters of the UUID to keep the UI clean
//            binding.profileId.text = "User ID: ${it.id.take(8).uppercase()}"
//            binding.txtFoundCount.text = it.treasureFound.toString()
//            binding.txtCreatedCount.text = it.treasureCreated.toString()
//        }
//}

