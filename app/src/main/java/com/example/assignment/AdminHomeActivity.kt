package com.example.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.assignment.AdminFragment.AdminActivityFragment
import com.example.assignment.AdminFragment.AdminFeedbackFragment
import com.example.assignment.AdminFragment.AdminHomeFragment
import com.example.assignment.AdminFragment.AdminManageUserFragment
import com.example.assignment.AdminFragment.AdminProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity_home)

        val adminHomeFragment = AdminHomeFragment()
        val adminActivityFragment = AdminActivityFragment()
        val adminManageUserFragment = AdminManageUserFragment()
        val adminFeedbackFragment = AdminFeedbackFragment()
        val adminProfileFragment = AdminProfileFragment()

        makeCurrentFragment(adminHomeFragment)

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