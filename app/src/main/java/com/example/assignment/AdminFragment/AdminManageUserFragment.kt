package com.example.assignment.AdminFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.assignment.R

class AdminManageUserFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.admin_fragment_manageuser, container, false)

        //Back button on screen
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Use the NavController to navigate to the specified action
                findNavController().navigate(R.id.action_adminManageUserFragment_to_adminLoginActivity)

            }
        }
        // Add the callback to the fragment's lifecycle
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        return view
    }


}