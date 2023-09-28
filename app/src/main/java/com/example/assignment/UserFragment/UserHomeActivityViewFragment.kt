package com.example.assignment.UserFragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Rect
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.assignment.Adapter.ActivityAdapter2
import com.example.assignment.AdminFragment.AdminHomeFragment
import com.example.assignment.Model.Activity
import com.example.assignment.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import kotlin.math.exp


class UserHomeActivityViewFragment : Fragment() {

    private lateinit var activityIdText: TextView
    private lateinit var activityNameText: TextView
    private lateinit var userNameText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var totalDonationText: TextView
    private lateinit var totalRequiredText: TextView
    private lateinit var statusText: TextView
    private lateinit var imageView: ImageView
    private lateinit var payButton: Button

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

        activityIdText    = view.findViewById(R.id.userHomeActivityIdView)
        activityNameText  = view.findViewById(R.id.userActivityDonateNameText)
        userNameText      = view.findViewById(R.id.userActivityViewUserIdText)
        descriptionText   = view.findViewById(R.id.userActivityViewDescriptionText)
        totalDonationText = view.findViewById(R.id.userActivityViewTtlDonatedText)
        totalRequiredText = view.findViewById(R.id.userActivityViewTtlRequiredText)
        statusText        = view.findViewById(R.id.user_homeView_activityStatus)
        imageView         = view.findViewById(R.id.user_home_activityView_image)

        // Populate the EditText fields with the existing data
        activityDetails?.let { activity ->
            activityIdText.text    = activity.id
            activityNameText.text  = activity.name
            userNameText.text      = activity.userId
            descriptionText.text   = activity.description
            totalDonationText.text = activity.totalDonationReceived
            totalRequiredText.text = activity.totalRequired
            statusText.text        = activity.status
            Glide.with(this)
                .load(activity.imageUrl) // Assuming activity.imageUrl contains the image URL
                .into(imageView)
        }

        val donateButton = view.findViewById<Button>(R.id.activityViewDonateButton)
        donateButton.setOnClickListener {
            showDonationDialog(activityIdText,totalDonationText)
        }

        view.findViewById<ImageView>(R.id.user_home_donation_deatils_backButton).setOnClickListener{
            if(statusText.text == "admin"){
                val bundle = Bundle()
                bundle.putString("activityCategory", "admin") // Put the data you want to pass here

                val receiverFragment = UserHomeFragment()
                receiverFragment.arguments = bundle

//                // Perform the fragment transaction to navigate to the receiving fragment
//                val transaction = fragmentManager?.beginTransaction()
//                transaction?.replace(R.id.user_fl_wrapper, receiverFragment)
//                transaction?.commit()
            }
            requireActivity().onBackPressed()
        }


        return view
    }

    private fun showDonationDialog(activityIdText: TextView, totalDonationText: TextView) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_payment_creditcard, null)
        val donationDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Payment Process...")
            .create()

        val payButton = dialogView.findViewById<Button>(R.id.btnPay)
        val closeButton = dialogView.findViewById<ImageView>(R.id.user_payDialog_closeButton)


        // Set a click listener for the "Close" image
        closeButton.setOnClickListener {
            donationDialog.dismiss() // Dismiss the dialog when Cancel is clicked
        }

        // Set a click listener for the "Donate" button
        payButton.setOnClickListener {

            // Disable focus on the dialog's input fields to prevent the keyboard from automatically showing
            donationDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


            val amount     = dialogView.findViewById<EditText>(R.id.etAmount).text.toString()
            val cardNumber = dialogView.findViewById<EditText>(R.id.etCardNumber).text.toString()
            val expiryDate = dialogView.findViewById<EditText>(R.id.etExpiryDate).text.toString()
            val cvv        = dialogView.findViewById<EditText>(R.id.etCvv).text.toString()

            val amountInputError     = dialogView.findViewById<TextInputLayout>(R.id.etAmountInputError)
            val cardNumberInputError = dialogView.findViewById<TextInputLayout>(R.id.etCardNumberInputError)
            val dateInputError       = dialogView.findViewById<TextInputLayout>(R.id.etExpiryDateInputError)
            val cvvInputError        = dialogView.findViewById<TextInputLayout>(R.id.etCvvInputError)

            if (amount.isEmpty()) {
                amountInputError.error = "Please enter an amount"
            } else {amountInputError.error = null}
            if (cardNumber.isEmpty() || cardNumber.length < 16) {
                cardNumberInputError.error = "Invalid card number"
            } else {cardNumberInputError.error = null}
            if (expiryDate.isEmpty() || !expiryDate.matches(Regex("\\d{2}/\\d{2}"))) {
                dateInputError.error = "Invalid expiry date (MM/YY)"
            } else {dateInputError.error = null}
            if (cvv.isEmpty() || !(cvv.length == 3 || cvv.length == 4)) {
                cvvInputError.error = "Invalid CVV"
            } else {cvvInputError.error = null}

            if(amountInputError.error == null && cardNumberInputError.error == null
                && dateInputError.error == null && cvvInputError.error == null) {

                // You can handle the donation process here
                // Update the donation details in the database

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
                    totalDonation = amount.toInt() + totalDonationText.text.toString().toInt()

                    // Get the current date
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-based, so add 1
                    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                    // Create a date string in your desired format (e.g., "yyyy-MM-dd")
                    val currentDate = "$year-$month-$dayOfMonth"

                    // Create a new donation document with the following fields:
                    val donationData = hashMapOf(
                        "userid" to userId,
                        "activityid" to activityId,
                        "amount_donate" to amount,
                        "cardNumber" to cardNumber,
                        "cardExpireDate" to expiryDate,
                        "cvv" to cvv,
                        "date" to currentDate
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