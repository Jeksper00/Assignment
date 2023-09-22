package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class UserLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity_login)

        findViewById<Button>(R.id.userLoginButton2).setOnClickListener{
            val intent = Intent(this, UserHomeActivity::class.java)
            startActivity(intent)
        }
    }
}