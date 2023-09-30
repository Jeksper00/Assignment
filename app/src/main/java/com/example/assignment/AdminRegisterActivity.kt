package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminRegisterActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity_register)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val adminName = findViewById<EditText>(R.id.adminRgtName)
        val adminEmail = findViewById<EditText>(R.id.adminRgtEmail)
        val adminPassword = findViewById<EditText>(R.id.adminRgtPassword)
        val adminConfirmPassword = findViewById<EditText>(R.id.adminConfirmPassword)
        val adminRegisterButton = findViewById<Button>(R.id.adminRegisterButton)
        val adminLoginPage = findViewById<TextView>(R.id.adminLoginPage)

        adminRegisterButton.setOnClickListener {
            val name = adminName.text.toString()
            val email = adminEmail.text.toString()
            val password = adminPassword.text.toString()
            val confirmPassword = adminConfirmPassword.text.toString()

            // Check if any of the fields are empty
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if passwords match
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Register the admin with Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration successful
                        val user = auth.currentUser

                        // Generate an admin ID
                        generateAdminId { adminId ->
                            // Save admin data to Firestore
                            val adminData = hashMapOf(
                                "id" to adminId,
                                "name" to name,
                                "email" to email
                            )

                            firestore.collection("admin")
                                .document(adminId)
                                .set(adminData)
                                .addOnSuccessListener {
                                    // Data successfully saved to Firestore
                                    val intent = Intent(this@AdminRegisterActivity, AdminLoginActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    // Handle error if data couldn't be saved to Firestore
                                    Toast.makeText(this@AdminRegisterActivity, "Error saving admin data: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        // Display an error message if registration fails
                        val exception = task.exception
                        Toast.makeText(this@AdminRegisterActivity, "Registration failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        adminLoginPage.setOnClickListener {
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun generateAdminId(callback: (String) -> Unit) {
        val adminIdPrefix = "M"
        firestore.collection("admin")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val adminCount = querySnapshot.size()
                val adminId = "$adminIdPrefix${String.format("%04d", adminCount + 1)}"
                callback(adminId)
            }
            .addOnFailureListener { exception ->
                // Handle any errors that may occur during data retrieval
                Toast.makeText(this@AdminRegisterActivity, "Error generating admin ID: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
