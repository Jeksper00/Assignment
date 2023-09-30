package com.example.assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.example.assignment.R

class UserEditProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var userNameEditText: EditText
    private lateinit var userEmailEditText: TextView
    private lateinit var userContactEditText: EditText
    private lateinit var userGenderEditText: EditText
    private lateinit var changePasswordTextView: TextView
    private lateinit var saveChangesButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_fragment_edit_profile, container, false)

        firestore = FirebaseFirestore.getInstance()

        userNameEditText = view.findViewById(R.id.userName)
        userEmailEditText = view.findViewById(R.id.userEmailAdd)
        userContactEditText = view.findViewById(R.id.userContact)
        userGenderEditText = view.findViewById(R.id.userGender)
        changePasswordTextView = view.findViewById(R.id.changePasswordPage)
        saveChangesButton = view.findViewById(R.id.saveButton)

        // Retrieve the userId from the arguments
        var userId = arguments?.getString("userid")
//        val userName = arguments?.getString("userName")
//        val userEmail = arguments?.getString("userEmail")
//        val userProfilePicUri = arguments?.getString("userProfilePicUri")

//        userNameEditText.setText(userId)
        if (userId != null) {
            // Retrieve user data from Firestore
            firestore.collection("user")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Get user data from Firestore
                        val username = documentSnapshot.getString("name")
                        val email = documentSnapshot.getString("email")
                        val contact = documentSnapshot.getString("contact")
                        val gender = documentSnapshot.getString("gender")

                        // Set retrieved data to the EditTexts
                        userNameEditText.setText(username)
                        userEmailEditText.setText(email)
                        userContactEditText.setText(contact)
                        userGenderEditText.setText(gender)
                    }
                }
                .addOnFailureListener { e ->
                    // Handle any errors that may occur during data retrieval
                }
        }

        changePasswordTextView.setOnClickListener {
            // Create an instance of the UserChangePasswordFragment
            val changePasswordFragment = UserChangePasswordFragment()

            // Use the FragmentManager to replace the current fragment with the UserChangePasswordFragment
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            // Replace the current fragment with the UserChangePasswordFragment
            transaction.replace(R.id.fragment_container, changePasswordFragment)

            // Optional: Add the transaction to the back stack to enable back navigation
            transaction.addToBackStack(null)

            // Commit the transaction
            transaction.commit()
        }


        // Handle the "Save Changes" button click
        saveChangesButton.setOnClickListener {
            val newName = userNameEditText.text.toString()
            val newContact = userContactEditText.text.toString()
            val newGender = userGenderEditText.text.toString()


            // Update user data in Firestore
            if (userId != null) {
                firestore.collection("user")
                    .document(userId)
                    .update(
                        "name" ,newName,
                        "contact", newContact,
                        "gender", newGender
                    )
                    .addOnSuccessListener {
                        Toast.makeText(context, "Updated Successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Updated Failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
            }
        }


        return view
    }
}
