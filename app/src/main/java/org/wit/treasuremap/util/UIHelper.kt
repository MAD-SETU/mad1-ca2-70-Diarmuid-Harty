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

/**
 * Extension function for Activity to dismiss the software keyboard.
 */
fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    // Find the currently focused view, or create a dummy one if none exists
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

// quick Snackbar
/**
 * Extension function to display a short Snackbar message on the screen.
 */
fun Activity.showSnackbar(message: String) {
    // Finds the root content view of the activity to attach the snackbar
    Snackbar.make(this.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
}

// toggle Visibility
/**
 * Toggles a View between VISIBLE and GONE.
 */
fun View.toggle() {
    this.visibility = if (this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

    /**
     * Toggles the visibility of the expandable menu and updates the toggle button icon.
     */
    fun ActivityTreasuremapBinding.toggleMenu() {
        // Toggle visibility of the menu layout
        controlLayout.expandableMenu.isVisible = !controlLayout.expandableMenu.isVisible
        // Switch the icon between 'close' (X) and 'add' (+) based on state
        val icon = if (controlLayout.expandableMenu.isVisible)
            android.R.drawable.ic_menu_close_clear_cancel else android.R.drawable.ic_menu_add
        controlLayout.btnMenuToggle.setImageResource(icon)
    }

    // Reset the Add Treasure UI back to normal
    /**
     * Resets the 'Add Treasure' input form and switches back to the main controls.
     */
    fun ActivityTreasuremapBinding.resetAddCard() {
        // Hide the input form and show the primary controls
        addTreasureLayout.root.isVisible = false
        controlLayout.root.isVisible = true
        // Clear text fields for the next entry
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

