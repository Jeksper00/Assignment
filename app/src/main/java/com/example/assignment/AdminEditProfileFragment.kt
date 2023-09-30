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

class AdminEditProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adminNameEditText: EditText
    private lateinit var adminEmailEditText: TextView
    private lateinit var changePasswordTextView: TextView
    private lateinit var saveChangesButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.admin_fragment_edit_profile, container, false)

        firestore = FirebaseFirestore.getInstance()

        adminNameEditText = view.findViewById(R.id.adminUsername)
        adminEmailEditText = view.findViewById(R.id.adminEmailAddress)
        changePasswordTextView = view.findViewById(R.id.changePwdPage)
        saveChangesButton = view.findViewById(R.id.saveChangesBtn)

         //Retrieve the adminId from the arguments
        var adminId = arguments?.getString("adminid")

       adminNameEditText.setText(adminId)
        if (adminId != null) {
            // Retrieve admin data from Firestore
            firestore.collection("admin")
                .document(adminId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                       //  Get admin data from Firestore
                        val username = documentSnapshot.getString("name")
                        val email = documentSnapshot.getString("email")

//                        // Set retrieved data to the EditTexts
                        adminNameEditText.setText(username)
                        adminEmailEditText.setText(email)
                   }
                }
                .addOnFailureListener { e ->
                    // Handle any errors that may occur during data retrieval
                }
        }

        changePasswordTextView.setOnClickListener {
            // Create an instance of the UserChangePasswordFragment
            val changePasswordFragment = AdminChangePasswordFragment()

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


         //Handle the "Save Changes" button click
        saveChangesButton.setOnClickListener {
            val newName = adminNameEditText.text.toString()

            // Update admin data in Firestore
            if (adminId != null) {
                firestore.collection("admin")
                    .document(adminId)
                    .update(
                        "name", newName,
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
