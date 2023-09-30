package com.example.assignment.AdminFragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.assignment.Adapter.UserListAdapter
import com.example.assignment.Adapter.UserListAdapter.Companion.ARG_NOTIFICATION
import com.example.assignment.Model.User
import com.example.assignment.R
import com.example.assignment.AdminFragment.AdminManageUserFragment
import com.google.firebase.firestore.FirebaseFirestore

class AdminUpdateUserDetailsFragment : Fragment() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var contactEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var updateButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.admin_fragment_updateuser, container, false)

        view.findViewById<ImageView>(R.id.user_details_backButton).setOnClickListener{
            requireActivity().onBackPressed()
        }

        val builder = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(requireActivity())

        // Retrieve the notification data from the arguments bundle
        val userToEdit = arguments?.getParcelable(ARG_NOTIFICATION) as User?

        nameEditText = view.findViewById(R.id.edit_username)
        emailEditText = view.findViewById(R.id.edit_email)
        contactEditText = view.findViewById(R.id.edit_contact)
        genderEditText = view.findViewById(R.id.edit_gender)
        passwordEditText = view.findViewById(R.id.edit_password)
        updateButton = view.findViewById(R.id.updateBtn)

        // Populate the EditText fields with the existing notification data
        userToEdit?.let { user ->
            nameEditText.setText(user.name)
            emailEditText.setText(user.email)
            contactEditText.setText(user.contactNo)
            genderEditText.setText(user.gender)
            passwordEditText.setText(user.password)
        }

        updateButton.setOnClickListener {
            // Handle the update button click here
            val updatedName = nameEditText.text.toString()
            val updatedEmail = emailEditText.text.toString()
            val updatedContact = contactEditText.text.toString()
            val updatedGender = genderEditText.text.toString()
            val updatedpassword = passwordEditText.text.toString()

            // Update the notification data in Firestore
            userToEdit?.let { user ->
                val db = FirebaseFirestore.getInstance()
                val notificationCollection = db.collection("user")

                val newUserDetails = hashMapOf(
                    "name" to updatedName,
                    "email" to updatedEmail,
                    "contact" to updatedContact,
                    "gender" to updatedGender,
                    "password" to updatedpassword
                )
                notificationCollection.document(user.id)
                    .set(newUserDetails)
                    .addOnSuccessListener {
                        // Data updated successfully
                        showSuccessDialog()
                        openFragment(AdminManageUserFragment())
                    }
                    .addOnFailureListener { exception ->
                        // Handle the failure to update data
                        showErrorDialog(exception.message)
                    }
            }
        }

        return view
    }

    private fun showSuccessDialog() {
        val successDialog = AlertDialog.Builder(requireContext())
            .setTitle("Success")
            .setMessage("User details updated successfully.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Navigate back to your notification list fragment
                // You can use fragmentManager.popBackStack() or other navigation methods
            }
            .create()

        successDialog.show()
    }

    private fun showErrorDialog(errorMessage: String?) {
        val errorDialog = AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage("Failed to update user details: $errorMessage")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        errorDialog.show()
    }

    private fun openFragment(fragment : Fragment){
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.admin_fl_wrapper, fragment)
        fragmentTransaction.addToBackStack(null) // Optional, to allow back navigation
        fragmentTransaction.commit()
    }
}