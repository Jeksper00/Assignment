package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class UserLoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity_login)

        auth = FirebaseAuth.getInstance()

        val userEmail = findViewById<EditText>(R.id.userEmail)
        val userPassword = findViewById<EditText>(R.id.userPassword)
        val loginButton = findViewById<Button>(R.id.userLoginButton2)
        val forgotPasswordPage = findViewById<TextView>(R.id.forgotPasswordPage)
        val registerPage = findViewById<TextView>(R.id.registerPage)

        loginButton.setOnClickListener {
            val enteredEmail = userEmail.text.toString()
            val enteredPassword = userPassword.text.toString()

            // Check if email and password are empty
            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Use Firebase Authentication to sign in
            auth.signInWithEmailAndPassword(enteredEmail, enteredPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Successful login, navigate to the home activity
                        val intent = Intent(this, UserHomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Display an error message if login fails
                        Toast.makeText(this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

//        forgotPasswordPage.setOnClickListener {
//            val intent = Intent(this, UserForgotPasswordActivity::class.java)
//            startActivity(intent)
//        }

        registerPage.setOnClickListener {
//            val intent = Intent(this, UserRegisterActivity::class.java)
//            startActivity(intent)
        }
    }
}