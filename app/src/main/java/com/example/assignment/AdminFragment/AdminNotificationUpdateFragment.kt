package com.example.assignment.AdminFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.assignment.R
import android.app.AlertDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.example.assignment.Adapter.NotificationAdapter.Companion.ARG_NOTIFICATION
import com.example.assignment.Model.Notification
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


class AdminNotificationUpdateFragment : Fragment() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var updateButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.admin_fragment_notification_update, container, false)

        view.findViewById<ImageView>(R.id.admin_notificationUpdate_backButton).setOnClickListener{
            requireActivity().onBackPressed()
        }

        val builder = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(requireActivity())

        // Set up a click listener for the main layout to close the keyboard when clicked outside
        val mainLayout = view.findViewById<View>(R.id.admin_notification_update_frame)
        mainLayout.setOnClickListener {
            // Hide the keyboard
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        // Retrieve the notification data from the arguments bundle
        val notificationToEdit = arguments?.getParcelable(ARG_NOTIFICATION) as Notification?

        titleEditText = view.findViewById(R.id.editTitleEditText)
        descriptionEditText = view.findViewById(R.id.editDescriptionEditText)
        updateButton = view.findViewById(R.id.updateButton)

        // Populate the EditText fields with the existing notification data
        notificationToEdit?.let { notification ->
            titleEditText.setText(notification.title)
            descriptionEditText.setText(notification.description)
        }

        // Get the current date
        val calendar   = Calendar.getInstance()
        val year       = calendar.get(Calendar.YEAR)
        val month      = calendar.get(Calendar.MONTH) + 1 // Months are 0-based, so add 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a date string in your desired format (e.g., "yyyy-MM-dd")
        val currentDate = "$year-$month-$dayOfMonth"

        updateButton.setOnClickListener {
            // Handle the update button click here
            val updatedTitle = titleEditText.text.toString()
            val updatedDescription = descriptionEditText.text.toString()

            val titleUpdateError =
                view.findViewById<TextInputLayout>(R.id.notificationUpdateTitleError)
            val descriptionUpdateError =
                view.findViewById<TextInputLayout>(R.id.notificationUpdateDescriptionError)


            if (updatedTitle.isEmpty()) {
                titleUpdateError.error = "Title field cannot be empty"
            } else {
                titleUpdateError.error = null
            }
            if (updatedDescription.isEmpty()) {
                descriptionUpdateError.error = "Description field cannot be empty"
            } else {
                descriptionUpdateError.error = null
            }

            if (titleUpdateError.error == null && descriptionUpdateError.error == null) {

                // Update the notification data in Firestore
                notificationToEdit?.let { notification ->
                    val db = FirebaseFirestore.getInstance()
                    val notificationCollection = db.collection("notification")

//                val updatedNotification = Notification(updatedTitle, updatedDescription)
                    val newNotification = hashMapOf(
                        "title" to updatedTitle,
                        "description" to updatedDescription,
                        "date" to currentDate
                    )
                    notificationCollection.document(notification.id)
                        .set(newNotification)
                        .addOnSuccessListener {
                            // Data updated successfully
                            //showSuccessDialog()
                            Toast.makeText(
                                requireContext(),
                                "Successfully Update Notification",
                                Toast.LENGTH_SHORT
                            ).show()
                            openFragment(AdminNotificationViewFragment())
                        }
                        .addOnFailureListener { exception ->
                            // Handle the failure to update data
                            showErrorDialog(exception.message)
                        }
                }
            }
        }

        return view
    }

    private fun showSuccessDialog() {
        val successDialog = AlertDialog.Builder(requireContext())
            .setTitle("Success")
            .setMessage("Notification updated successfully.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()

            }
            .create()

        successDialog.show()
    }

    private fun showErrorDialog(errorMessage: String?) {
        val errorDialog = AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage("Failed to update notification: $errorMessage")
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