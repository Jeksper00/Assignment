package com.example.assignment

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.assignment.R

class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userEmailEditText: EditText
    private lateinit var sendEmailButton: Button
    private lateinit var resendVerificationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity_forgot_password)

        auth = FirebaseAuth.getInstance()
        userEmailEditText = findViewById(R.id.emailUser)
        sendEmailButton = findViewById(R.id.sendButton)
        resendVerificationButton = findViewById(R.id.resendVerificationButton)

        val user = auth.currentUser

        // Check if the user is signed in
        if (user != null) {
            // Check if the email is verified
            if (user.isEmailVerified) {
                // Email is already verified; allow the user to access protected content.
                showToast("Email is verified. You can access your account.")
            } else {
                // Email is not verified; prompt the user to verify and optionally resend the email.
                showToast("Email is not verified. Please check your email and verify your account.")
                setupResendVerificationButton(user)
            }
        }

        // Handle the "Send" button click
        sendEmailButton.setOnClickListener {
            val email = userEmailEditText.text.toString()

            if (email.isNotEmpty()) {
                // Send email verification to the user
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("Verification email sent. Please check your email.")
                        } else {
                            showToast("Failed to send verification email. Please try again later.")
                        }
                    }
            } else {
                showToast("Please enter your email address.")
            }
        }
    }

    private fun setupResendVerificationButton(user: FirebaseUser) {
        resendVerificationButton.setOnClickListener {
            // Resend the verification email
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Verification email sent. Please check your email.")
                    } else {
                        showToast("Failed to send verification email. Please try again later.")
                    }
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
