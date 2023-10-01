package com.example.assignment

import android.content.ContentValues
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.assignment.AdminFragment.AdminActivityFragment
import com.example.assignment.AdminFragment.AdminFeedbackFragment
import com.example.assignment.AdminFragment.AdminHomeFragment
import com.example.assignment.AdminFragment.AdminManageUserFragment
import com.example.assignment.AdminFragment.AdminProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.util.Objects

class AdminHomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity_home)

        val adminHomeFragment = AdminHomeFragment()
        val adminActivityFragment = AdminActivityFragment()
        val adminManageUserFragment = AdminManageUserFragment()
        val adminFeedbackFragment = AdminFeedbackFragment()
        val adminProfileFragment = AdminProfileFragment()

        // Retrieve the data from the intent
        val receivedData = intent.getStringExtra("fragmentToOpen")

        // Check if the data is not null before using it
        if (receivedData != null) {
            if (receivedData == "Activity") {
                // Now you can use the receivedData in your activity

//                // Assuming you have a NavigationView with an id of 'navigationView'
//                val navigationView = findViewById<NavigationView>(R.id.admin_bottom_navigation)
//                // Set the selected item by passing the ID of the menu item
//                navigationView.setCheckedItem(R.id.admin_icon_activity)
                makeCurrentFragment(adminActivityFragment)
                Log.e(ContentValues.TAG, "Success")
            }
        }else{
            makeCurrentFragment(adminHomeFragment)
            Log.e(ContentValues.TAG, "Fails")
        }


//        makeCurrentFragment(adminHomeFragment)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.admin_bottom_navigation)
        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.admin_icon_home -> makeCurrentFragment(adminHomeFragment)
                R.id.admin_icon_activity -> makeCurrentFragment(adminActivityFragment)
                R.id.admin_icon_manage_accounts -> makeCurrentFragment(adminManageUserFragment)
                R.id.admin_icon_feedback -> makeCurrentFragment(adminFeedbackFragment)
                R.id.admin_icon_profile -> makeCurrentFragment(adminProfileFragment)
            }
            true
        }




    }

    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.admin_fl_wrapper, fragment)
        commit()
    }
}