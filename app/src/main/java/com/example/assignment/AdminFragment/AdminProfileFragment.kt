package com.example.assignment.AdminFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.assignment.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide
import com.example.assignment.AdminEditProfileFragment
import com.example.assignment.AdminLoginActivity
import com.example.assignment.UserEditProfileFragment


class AdminProfileFragment : Fragment() {

    private lateinit var adminNameTextView: TextView
    private lateinit var adminEmailTextView: TextView
    private lateinit var adminProfileImageView: ImageView
    private lateinit var adminEditDetails: TextView
    private lateinit var editButton: ImageButton
    private lateinit var logoutBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.admin_fragment_profile, container, false)

        adminNameTextView = view.findViewById(R.id.adminName)
        adminEmailTextView = view.findViewById(R.id.adminEmailAdd)
        adminProfileImageView = view.findViewById(R.id.adminProfilePic)
        adminEditDetails = view.findViewById(R.id.txtEditDetail)
        editButton = view.findViewById(R.id.editButton)
        logoutBtn = view.findViewById(R.id.adminLogoutButton)

        val admin = FirebaseAuth.getInstance().currentUser
        val adminId = admin?.uid
        val firestore = FirebaseFirestore.getInstance()
        var adminFid=""
        val auth = FirebaseAuth.getInstance()

        if (admin != null) {
            // Retrieve user's name and email from Firebase Authentication
            val adminName = admin.displayName
            val adminEmail = admin.email

            if (adminEmail != null) {
                // Query Firestore to get the user with the matching email
                firestore.collection("admin")
                    .whereEqualTo("email", adminEmail)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot) {
                            // Get the admin's ID from the document
                            adminFid = document.id
                            adminNameTextView.text = document.getString("name")
                            adminEmailTextView.text = adminEmail

                            val profileImageUrl = document.getString("profileImageURL")
                            if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
                                // Load and display the profile picture using Glide
                                var into: Any = Glide.with(this)
                                    .load(profileImageUrl)
                                    .into(adminProfileImageView)
                            } else {
                                // Admin hasn't set a profile picture, display default image
                                adminProfileImageView.setImageResource(R.drawable.icon_add_image)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle any errors that may occur during the query
                    }
            }

        }
        editButton.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("adminid", adminFid)
            val fragment = AdminEditProfileFragment()
            fragment.setArguments(bundle)

            // Get the FragmentManager for the parent Activity
            val fragmentManager = requireActivity().supportFragmentManager

            // Create a FragmentTransaction
            val fragmentTransaction = fragmentManager.beginTransaction()

            // Replace the current fragment with the new fragment
            fragmentTransaction.replace(R.id.admin_fl_wrapper, fragment)

            // Add the transaction to the back stack
            fragmentTransaction.addToBackStack(null)

            // Commit the transaction
            fragmentTransaction.commit()

        }

        logoutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), AdminLoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
        }
}
