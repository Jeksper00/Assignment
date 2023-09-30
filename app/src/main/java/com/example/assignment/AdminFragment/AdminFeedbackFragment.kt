package com.example.assignment.AdminFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.example.assignment.R


class AdminFeedbackFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.admin_fragment_feedback, container, false)

        //Back button on screen
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Use the NavController to navigate to the specified action
                findNavController().navigate(R.id.action_adminFeedbackFragment_to_adminLoginActivity)

            }
        }
        // Add the callback to the fragment's lifecycle
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        view.findViewById<Button>(R.id.adminViewFeedbackBotton).setOnClickListener{
            openFragment(AdminFeedbackViewFragment())
        }

        view.findViewById<Button>(R.id.adminViewNotificationButton).setOnClickListener{
            openFragment(AdminNotificationViewFragment())
        }

        return view
    }

    private fun openFragment(fragment : Fragment){
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.admin_fl_wrapper, fragment)
        fragmentTransaction.addToBackStack(null) // Optional, to allow back navigation
        fragmentTransaction.commit()
    }



}