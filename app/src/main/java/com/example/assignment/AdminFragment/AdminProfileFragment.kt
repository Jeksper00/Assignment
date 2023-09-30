package com.example.assignment.AdminFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.assignment.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide
import com.example.assignment.AdminEditProfileFragment
import com.example.assignment.UserEditProfileFragment


class AdminProfileFragment : Fragment() {

    private lateinit var adminNameTextView: TextView
    private lateinit var adminEmailTextView: TextView
    private lateinit var adminProfileImageView: ImageView
    private lateinit var adminEditDetails: TextView
    private lateinit var editButton: ImageButton

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

        val admin = FirebaseAuth.getInstance().currentUser
        val adminId = admin?.uid
        val firestore = FirebaseFirestore.getInstance()
        var adminFid=""

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
                            val adminIdFromFirestore = document.id
                            adminFid = document.id
                            adminNameTextView.text = document.getString("name")
                            adminEmailTextView.text = adminEmail
                            if (adminId != null) {
                                firestore.collection("admin").document(adminId)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            // Update UI with admin's name and email

                                            val profilePictureUrl = document.getString("profilePictureUrl")
                                            if (profilePictureUrl != null && profilePictureUrl.isNotEmpty()) {
                                                // Load and display the profile picture using Glide
                                                Glide.with(requireContext())
                                                    .load(profilePictureUrl)
                                                    .into(adminProfileImageView)
                                            } else {
                                                // User hasn't set a profile picture, display default image
                                                adminProfileImageView.setImageResource(R.drawable.icon_add_photo)
                                            }
                                        } else {
                                            // Handle the case where the document does not exist
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        // Handle any errors that may occur during data retrieval
                                    }
                            }


                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle any errors that may occur during the query
                    }
            }

        }
        editButton.setOnClickListener{
//            val intent = Intent(requireContext(), UserEditProfileFragment::class.java)
//            intent.putExtra("userId", userId)
//            this.startActivity(intent)
//            fragmentManager.beginTransaction()
//                .replace(R.id.user_fl_wrapper, UserEditProfileFragment())
//                .addToBackStack(null)
//                .commit()

            val bundle = Bundle()
            bundle.putString("adminid", adminFid)
            val fragment = AdminEditProfileFragment()
            fragment.setArguments(bundle)
//            bundle.putString("userName", username)
//            bundle.putString("userEmail", userEmail)
//            bundle.putString("userProfilePicUri", userProfilePicUri)

            // Get the FragmentManager for the parent Activity
            val fragmentManager = requireActivity().supportFragmentManager

            // Create a FragmentTransaction
            val fragmentTransaction = fragmentManager.beginTransaction()

            // Replace the current fragment with the new fragment
//            val newFragment = UserEditProfileFragment()
            fragmentTransaction.replace(R.id.admin_fl_wrapper, fragment)

            // Add the transaction to the back stack (optional)
            fragmentTransaction.addToBackStack(null)

            // Commit the transaction
            fragmentTransaction.commit()


        }
        return view
    }
}