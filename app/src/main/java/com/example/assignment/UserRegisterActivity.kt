package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class UserRegisterActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity_register)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val username = findViewById<EditText>(R.id.userRgtName)
        val userEmail = findViewById<EditText>(R.id.userRgtEmail)
        val userPassword = findViewById<EditText>(R.id.userRgtPassword)

        val userConfirmPassword = findViewById<EditText>(R.id.userConfirmPassword)
        val registerButton = findViewById<Button>(R.id.userRegisterButton)
        val loginPage = findViewById<TextView>(R.id.loginPage)

        registerButton.setOnClickListener {
            val name = username.text.toString()
            val email = userEmail.text.toString()
            val password = userPassword.text.toString()
            val confirmPassword = userConfirmPassword.text.toString()
            val contact = ""
            val gender = ""


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

            generateUserId { userId ->
                // Register the user with Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration successful
                            val user = auth.currentUser
                            val userUid= auth.uid

                            // Save user data to Firestore
                            val userData = hashMapOf(
                                "id" to userUid,
                                "name" to name,
                                "email" to email,
                                "contact" to contact,
                                "gender" to gender,
                                "password" to password
                            )

                            firestore.collection("user")
                                .document(userId)
                                .set(userData)
                                .addOnSuccessListener {
                                    // Data successfully saved to Firestore
                                    val intent = Intent(this@UserRegisterActivity, UserLoginActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    // Handle error if data couldn't be saved to Firestore
                                    Toast.makeText(this@UserRegisterActivity, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Registration failed, display an error message
                            val exception = task.exception
                            Toast.makeText(this@UserRegisterActivity, "Registration failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        loginPage.setOnClickListener {
            val intent = Intent(this, UserLoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun generateUserId(callback: (String) -> Unit) {
        val userIdPrefix = "U"
        firestore.collection("user")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val userCount = querySnapshot.size()
                val userId = "$userIdPrefix${String.format("%04d", userCount + 1)}"
                callback(userId)
            }
            .addOnFailureListener { exception ->
                // Handle any errors that may occur during data retrieval
                Toast.makeText(this@UserRegisterActivity, "Error generating user ID: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
