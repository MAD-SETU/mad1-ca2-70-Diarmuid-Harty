package org.wit.treasuremap.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.wit.treasuremap.R
import org.wit.treasuremap.databinding.ActivityLoginBinding
import org.wit.treasuremap.main.MainApp
import org.wit.treasuremap.util.hideKeyboard
import org.wit.treasuremap.util.showSnackbar
import timber.log.Timber.i

/**
 * Activity responsible for user authentication using Google Sign-In and Firebase.
 */
class LoginActivity : AppCompatActivity() {

    // Binding for activity_login layout to access UI elements
    private lateinit var binding: ActivityLoginBinding
    // Reference to global application class for shared state
    lateinit var app: MainApp

    // firebase and google auth vars
    // Handles Firebase-specific authentication tasks
    private lateinit var auth: FirebaseAuth
    // Handles Google Sign-In UI and intent generation
    private lateinit var  googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Set up View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize global app reference from context
        app = application as MainApp

        // Init firebase auth
        // Get the singleton instance of FirebaseAuth for this project
        auth = FirebaseAuth.getInstance()

        // configure google sign in using web client id from strings.xml
        // This Client ID links the Android app to the Firebase Console project
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Request token for Firebase exchange
            .requestEmail() // Ask for user's email address permissions
            .build()

        // Create the GoogleSignInClient with the options specified above
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        i("Login Activity Started")

        // // --- LOGIN BUTTON ---
        // Triggers the Google Sign-In process when the user clicks the login button
        binding.btnLogin.setOnClickListener {
            hideKeyboard() // Utility to dismiss the soft keyboard for better visibility
            // Create the intent that launches the account selection screen
            val signInIntent = googleSignInClient.signInIntent
            // Launch the account picker via the registered activity result launcher
            intentLauncher.launch(signInIntent)
        }
    }

    // Fully AI Code
    // Result Launcher: Catches the result when the user finishes picking an account
    // This modern API replaces the older onActivityResult pattern
    private val intentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Extract the signed-in account information from the result intent
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            // Attempt to get the account, throwing an exception if login failed
            val account = task.getResult(ApiException::class.java)!!
            i("Google sign in successful: ${account.email}")
            // Now exchange this Google account for a Firebase User to create a session
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Handle common failures (e.g., user cancelled, no internet)
            i("Google sign in failed: Error Code ${e.statusCode}")
            showSnackbar("Google Sign In Failed")
        }
    }

    // Fully AI Code
    // Firebase Handshake: Creates the actual session
    /**
     * Exchanges a Google ID Token for a Firebase Credential to sign into Firebase.
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        // Create a Firebase credential from the Google token
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        // Use the credential to sign in to the Firebase Auth service
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in successful, update UI with the signed-in user's information
                    val user = auth.currentUser
                    i("Firebase login successful for UID: ${user?.uid}")

                    // Proceed to the main activity (the map screen)
                    viewMap()
                } else {
                    // Authentication failed (e.g., account disabled, error on Firebase side)
                    i("Firebase authentication failed")
                    showSnackbar("Authentication Failed")
                }
            }
    }

    // had AI help with this but have since found it in class material
    /**
     * Transitions from Login to the main Map Activity.
     */
    private fun viewMap() {
        val intent = Intent(this, TreasuremapActivity::class.java)
        startActivity(intent)
        // Call finish() so the user can't navigate back to the login screen with the back button
        finish() // Prevents going back to log in screen on back press
    }
}
