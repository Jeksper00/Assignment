package com.example.assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class UserEditProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var userNameEditText: EditText
    private lateinit var userEmailEditText: TextView
    private lateinit var userContactEditText: EditText
    private lateinit var userGenderEditText: EditText
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
        saveChangesButton = view.findViewById(R.id.saveButton)


        //Retrieve the userId from the arguments
        val userId = arguments?.getString("userId")
        val bundle = arguments
        val userId2 = bundle?.getString("userId")

        userGenderEditText.setText(userId)

        if (userId != null) {
            // Retrieve user data from Firestore
            firestore.collection("user")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Get user data from Firestore
                        val username = documentSnapshot.getString("username")
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

        // Handle the "Save Changes" button click
        saveChangesButton.setOnClickListener {
            val newContact = userContactEditText.text.toString()
            val newGender = userGenderEditText.text.toString()

            // Update user data in Firestore
            if (userId != null) {
                val dataToUpdate = hashMapOf(
                    "contact" to newContact,
                    "gender" to newGender
                )
            }
        }
        return view
    }
}
