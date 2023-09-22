package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.userLoginButton).setOnClickListener{
            val intent = Intent(this, UserLoginActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.adminLoginButton).setOnClickListener{
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
        }

    }



}