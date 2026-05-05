package org.wit.treasuremap.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber.i
import org.wit.treasuremap.main.MainApp
import org.wit.treasuremap.databinding.ActivityLoginBinding
import org.wit.treasuremap.models.UserModel
import org.wit.treasuremap.util.getUser
import org.wit.treasuremap.util.hideKeyboard
import org.wit.treasuremap.util.showSnackbar

//todo: add real user check and security and stuff later
class LoginActivity : AppCompatActivity() {
    lateinit var app: MainApp
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MainApp

        i("Login Activity Started")

        // // --- LOGIN BUTTON ---
        binding.btnLogin.setOnClickListener {

            hideKeyboard()

            // take name as val first for validation
            val input = binding.usernameField.text.toString().trim()

            // if is empty, tell and stop
            if (input.isEmpty()) {
                showSnackbar("Enter a username")
                return@setOnClickListener
            }

            // if name not empty and exists
            else {
                val user = getUser(input, app.users) // assign temp user for further validation

                // if user isn't null it is valid
                // (as I don't check for anything other than a correct name yet)
                if (user != null) {
                    app.currentUser = user // assign the user to active user in MainApp.kt
                    viewMap() // done here, switch to next view
                }

                // if name isn't found in users array
                // tell user to click add account button if they want a new account with this username
                if (user == null) {
                    showSnackbar("Username not linked to an existing account,\n" +
                            " Click Create Account to create account as $input")
                }
            }
        }

        // --- CREATE USER BUTTON ---
        binding.btnAddUser.setOnClickListener {

            hideKeyboard()

            // take input
            val input = binding.usernameField.text.toString().trim()

            // if is empty, tell and stop
            if (input.isEmpty()) {
                showSnackbar("Enter a username")
                return@setOnClickListener
            } else {
                val user = getUser(input, app.users) // assign temp user for further validation

                // if user isn't null it exists in array and cannot be assigned as new username
                if (user != null) {
                    showSnackbar("Username: ${input}, is already registered,\n" +
                            " enter a unique username for account creation")
                } else {
                    // username hasn't been used so is valid, store it here
                    val newUser = UserModel(username = input)

                    // add new user object to array in MainApp,kt
                    app.users.create(newUser.copy())
                    i("New User Created: ${newUser.username} with ID ${newUser.id}")

                    // assign active user in mainApp
                    app.currentUser = newUser
                    viewMap() // done here, switch to next view
                }
            }
        }
    }

    // had AI help with this but have since found it in class material
    private fun viewMap() {
        val intent = Intent(this, TreasuremapActivity::class.java)
        startActivity(intent)
        finish() // Prevents going back to log in screen on back press
    }

}