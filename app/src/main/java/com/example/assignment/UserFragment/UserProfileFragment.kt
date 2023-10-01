package com.example.assignment.UserFragment

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
import com.example.assignment.UserEditProfileFragment
import com.example.assignment.UserLoginActivity

class UserProfileFragment : Fragment() {

    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userProfileImageView: ImageView
    private lateinit var userEditDetails: TextView
    private lateinit var editBtn: ImageButton
    private lateinit var logoutBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_fragment_profile, container, false)

        userNameTextView = view.findViewById(R.id.username)
        userEmailTextView = view.findViewById(R.id.userEmailAddress)
        userProfileImageView = view.findViewById(R.id.userProfilePic)
        userEditDetails = view.findViewById(R.id.textEditDetails)
        editBtn = view.findViewById(R.id.editBtn)
        logoutBtn = view.findViewById(R.id.logoutButton)

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid
        val firestore = FirebaseFirestore.getInstance()
        var userFid = ""
        val auth = FirebaseAuth.getInstance()

        if (user != null) {
            // Retrieve user's name and email from Firebase Authentication
            val userName = user.displayName
            val userEmail = user.email

            if (userEmail != null) {
                // Query Firestore to get the user with the matching email
                firestore.collection("user")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot) {
                            // Get the user's ID from the document
                            userFid = document.id
                            userNameTextView.text = document.getString("name")
                            userEmailTextView.text = userEmail

                            val profileImageURL =
                                document.getString("profileImageURL")
                            if (profileImageURL != null && profileImageURL.isNotEmpty()) {
                                // Load and display the profile picture using Glide
                                var into: Any = Glide.with(this)
                                    .load(profileImageURL)
                                    .into(userProfileImageView)
                            } else {
                                // User hasn't set a profile picture, display default image
                                userProfileImageView.setImageResource(R.drawable.icon_add_photo)
                            }

                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle any errors that may occur during the query
                    }
            }
        }

        editBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userid", userFid)
            val fragment = UserEditProfileFragment()
            fragment.arguments = bundle

            // Get the FragmentManager for the parent Activity
            val fragmentManager = requireActivity().supportFragmentManager

            // Create a FragmentTransaction
            val fragmentTransaction = fragmentManager.beginTransaction()

            // Replace the current fragment with the new fragment
            fragmentTransaction.replace(R.id.user_fl_wrapper, fragment)

            // Add the transaction to the back stack
            fragmentTransaction.addToBackStack(null)

            // Commit the transaction
            fragmentTransaction.commit()
        }

        logoutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), UserLoginActivity::class.java)
            startActivity(intent)
        }

        return view
        }
}
