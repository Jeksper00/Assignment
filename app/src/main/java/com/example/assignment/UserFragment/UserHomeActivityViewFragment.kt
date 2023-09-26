package com.example.assignment.UserFragment

import android.app.AlertDialog
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.assignment.Adapter.ActivityAdapter2
import com.example.assignment.Model.Activity
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore


class UserHomeActivityViewFragment : Fragment() {

    private lateinit var activityIdText: TextView
    private lateinit var activityNameText: TextView
    private lateinit var userNameText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var totalDonationText: TextView
    private lateinit var imageView: ImageView
    private lateinit var donateButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.user_fragment_home_activity_view, container, false)


        val builder = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(requireActivity())

        // Retrieve the notification data from the arguments bundle
        val activityDetails = arguments?.getParcelable(ActivityAdapter2.ARG_NOTIFICATION) as Activity?

        activityIdText = view.findViewById(R.id.userHomeActivityIdView)
        activityNameText = view.findViewById(R.id.userActivityDonateNameText)
        userNameText = view.findViewById(R.id.userActivityViewUserIdText)
        descriptionText = view.findViewById(R.id.userActivityViewDescriptionText)
        totalDonationText = view.findViewById(R.id.userActivityViewTtlDonatedText)
        imageView = view.findViewById(R.id.user_home_activityView_image)

        // Populate the EditText fields with the existing data
        activityDetails?.let { activity ->
            activityIdText.text = activity.id
            activityNameText.text = activity.name
            userNameText.text = activity.userId
            descriptionText.text = activity.description
            totalDonationText.text = activity.totalDonationReceived
            Glide.with(this)
                .load(activity.imageUrl) // Assuming activity.imageUrl contains the image URL
                .into(imageView)
        }

        val donateButton = view.findViewById<Button>(R.id.activityViewDonateButton)
        donateButton.setOnClickListener {
            showDonationDialog(activityIdText,totalDonationText)
        }


        return view
    }

    private fun showDonationDialog(activityIdText: TextView, totalDonationText: TextView) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_donation_dialog, null)
        val donationDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Please select amount to donate")
            .create()

        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.donationRadioGroup)
        val donateButton = dialogView.findViewById<Button>(R.id.donateButton)

        // Set a click listener for the "Donate" button
        donateButton.setOnClickListener {
            // Get the selected donation amount from the radio group
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val selectedRadioButton = radioGroup.findViewById<RadioButton>(selectedRadioButtonId)
            val donationAmount = selectedRadioButton?.text.toString()

            // You can handle the donation process here
            // Update the donation details in the database
            val radioGroup = dialogView.findViewById<RadioGroup>(R.id.donationRadioGroup)
            val donateButton = dialogView.findViewById<Button>(R.id.donateButton)

            // Get the activity details from the arguments bundle
            val activityDetails =
                arguments?.getParcelable(ActivityAdapter2.ARG_NOTIFICATION) as Activity?

            // Check if activityDetails is not null
            if (activityDetails != null) {
                // Create a Firestore reference to the "donations" collection
                val db = FirebaseFirestore.getInstance()
                val donationsCollection = db.collection("donation")
                val activityCollection = db.collection("activity")

                val userId = "001"
                val activityId = activityIdText.text

                var totalDonation = 0
                totalDonation = donationAmount.toInt() + totalDonationText.text.toString().toInt()

                // Create a new donation document with the following fields:
                val donationData = hashMapOf(
                    "userid" to userId,
                    "activityid" to activityId,
                    "amount_donate" to donationAmount
                )

//                val updateActivity = hashMapOf(
//                    "totalDonationReceived" to totalDonation.toString()
//                )

                activityCollection.document(activityId.toString())
                    .update("totalDonationReceived", totalDonation.toString())

                // Add the donation document to Firestore
                donationsCollection.add(donationData)
                    .addOnSuccessListener { documentReference ->
                        showSuccessDialog()

                        totalDonationText.text = totalDonation.toString()

                        // Close the dialog
                        donationDialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        // Error occurred while adding the donation to Firestore
                        // Handle the error and display an error message if needed
                        // ...

                        // Close the dialog
                        donationDialog.dismiss()
                    }
            }
        }

        donationDialog.show()
    }

    private fun showSuccessDialog() {
        val successDialog = AlertDialog.Builder(requireContext())
            .setTitle("Success")
            .setMessage("Donate successfully.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Navigate back to your notification list fragment
                // You can use fragmentManager.popBackStack() or other navigation methods

                // Refresh the fragment by popping it from the back stack and navigating to it again
//                fragmentManager?.popBackStack()
//                fragmentManager?.beginTransaction()
//                    ?.replace(R.id.user_fl_wrapper, this)
//                    ?.addToBackStack(null)
//                    ?.commit()
            }
            .create()

        successDialog.show()
    }

    private fun showErrorDialog(errorMessage: String?) {
        val errorDialog = AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage("Failed to donate: $errorMessage")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        errorDialog.show()
    }
}