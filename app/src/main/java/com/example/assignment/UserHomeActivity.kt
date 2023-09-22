package com.example.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.assignment.UserFragment.UserActivityFragment
import com.example.assignment.UserFragment.UserHomeFragment
import com.example.assignment.UserFragment.UserNotificationFragment
import com.example.assignment.UserFragment.UserProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class UserHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity_home)

        val userHomeFragment = UserHomeFragment()
        val userActivityFragment = UserActivityFragment()
        val userNotificationFragment = UserNotificationFragment()
        val userProfileFragment = UserProfileFragment()

        makeCurrentFragment(userHomeFragment)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.user_bottom_navigation)
        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.user_icon_home -> makeCurrentFragment(userHomeFragment)
                R.id.user_icon_activity -> makeCurrentFragment(userActivityFragment)
                R.id.user_icon_notifications -> makeCurrentFragment(userNotificationFragment)
                R.id.user_icon_profile -> makeCurrentFragment(userProfileFragment)
            }
            true
        }

    }

    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.user_fl_wrapper, fragment)
        commit()
    }
}