package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AdminLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity_login)

        findViewById<Button>(R.id.adminLoginButton2).setOnClickListener{
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
        }
    }
}