package com.example.assignment.AdminFragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.example.assignment.R
import com.example.assignment.UserFragment.UserNotificationFragment
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.UUID


class AdminNotificationCreateFragment : Fragment() {

    private var db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.admin_fragment_notification_create, container, false)

        // Set up a click listener for the main layout to close the keyboard when clicked outside
        val mainLayout = view.findViewById<View>(R.id.admin_notification_create_frame)
        mainLayout.setOnClickListener {
            // Hide the keyboard
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        view.findViewById<ImageView>(R.id.admin_notificationCreate_backButton).setOnClickListener{
            requireActivity().onBackPressed()
        }

        view.findViewById<Button>(R.id.adminAddNotificationBtn).setOnClickListener {

            val notificationTitle =
                view.findViewById<EditText>(R.id.notificationTitleText).text.toString()
            val notificationDetails =
                view.findViewById<EditText>(R.id.notificationDetails).text.toString()

            val titleInputError = view.findViewById<TextInputLayout>(R.id.notificationInputTitleError)
            val descriptionInputError = view.findViewById<TextInputLayout>(R.id.notificationInputDescriptionError)


            if (notificationTitle.isEmpty()) {
                titleInputError.error    = "Title field cannot be empty"
            } else {
                titleInputError.error    = null
            }
            if (notificationDetails.isEmpty()) {
                descriptionInputError.error = "Description field cannot be empty"
            } else {
                descriptionInputError.error = null
            }

            if(titleInputError.error == null && descriptionInputError.error == null) {

                val currentUser = FirebaseAuth.getInstance().currentUser

//            if (currentUser != null) {
//                val userId = currentUser.uid

                // Get the current date
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-based, so add 1
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                // Create a date string in your desired format (e.g., "yyyy-MM-dd")
                val currentDate = "$year-$month-$dayOfMonth"

                val notification = hashMapOf(
                    "title" to notificationTitle,
                    "description" to notificationDetails,
                    "date" to currentDate
                )

//                db.collection("feedback").document("").set(feedback)
                db.collection("notification").add(notification)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Successfully Create Notification",
                            Toast.LENGTH_SHORT
                        ).show()
                        openFragment(AdminNotificationViewFragment())
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Failed To Create Notification!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


//            } else {
//                // Handle the case where the user is not signed in
//                //Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
//            }
            }
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