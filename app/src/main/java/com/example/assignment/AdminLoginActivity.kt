package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminLoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity_login)

        //Back button on screen
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Your custom logic here
                // For example, navigate to a different activity
                val intent = Intent(this@AdminLoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        auth = FirebaseAuth.getInstance()

        val adminEmail = findViewById<EditText>(R.id.adminEmail)
        val adminPassword = findViewById<EditText>(R.id.adminPassword)
        val adminLoginButton = findViewById<Button>(R.id.adminLoginButton2)
        val adminRegisterPage = findViewById<TextView>(R.id.adminRegisterPage)

        adminLoginButton.setOnClickListener {
            val enteredEmail = adminEmail.text.toString()
            val enteredPassword = adminPassword.text.toString()

            // Check if email and password are empty
            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = Firebase.firestore
            val adminCollection = db.collection("admin")

            // Use Firebase Authentication to sign in
            auth.signInWithEmailAndPassword(enteredEmail, enteredPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        adminCollection.get().addOnSuccessListener { querySnapshot ->
                            var emailFound = false
                            for (document in querySnapshot) {
                                val email            = document.getString("email") ?: ""
                                if(email == enteredEmail){
                                    // Successful login, navigate to the admin home activity
                                    val intent = Intent(this, AdminHomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    emailFound = true
                                    break
                                }
                            }
                            if (!emailFound) {
                                Toast.makeText(this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Display an error message if login fails
                        Toast.makeText(
                            this,
                            "Login failed. Please check your credentials.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        adminRegisterPage.setOnClickListener {
            val intent = Intent(this, AdminRegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // Clear the text fields when the activity resumes
        val adminEmail = findViewById<EditText>(R.id.adminEmail)
        val adminPassword = findViewById<EditText>(R.id.adminPassword)

        adminEmail.text.clear()
        adminPassword.text.clear()
    }
}
