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

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var app: MainApp

    // firebase and google auth vars
    private lateinit var auth: FirebaseAuth
    private lateinit var  googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        app = application as MainApp

        // Init firebase auth
        auth = FirebaseAuth.getInstance()

        // configure google sign in using web client id from strings.xml
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        i("Login Activity Started")

        // // --- LOGIN BUTTON ---
        binding.btnLogin.setOnClickListener {
            hideKeyboard()
            // take name as val first for validation
            val signInIntent = googleSignInClient.signInIntent
            intentLauncher.launch(signInIntent)
        }
    }

    // Fully AI Code
    // Result Launcher: Catches the result when the user finishes picking an account
    private val intentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            i("Google sign in successful: ${account.email}")
            // Now exchange this Google account for a Firebase User
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            i("Google sign in failed: Error Code ${e.statusCode}")
            showSnackbar("Google Sign In Failed")
        }
    }

    // Fully AI Code
    // Firebase Handshake: Creates the actual session
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    i("Firebase login successful for UID: ${user?.uid}")

                    // Proceed to the main map
                    viewMap()
                } else {
                    i("Firebase authentication failed")
                    showSnackbar("Authentication Failed")
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