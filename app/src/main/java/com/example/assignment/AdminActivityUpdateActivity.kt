package com.example.assignment

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminActivityUpdateActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage

    private lateinit var nameText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var totalRequireText: EditText
    private lateinit var calendarView: CalendarView
    private lateinit var userIdText: TextView
    private lateinit var activityIdText: TextView
    private lateinit var statusText: TextView
    private lateinit var totalReceiveText: TextView
    private lateinit var imageView: ImageView


    private lateinit var updateButton: Button


    // Declare public properties for Firestore document fields
    var id: String = ""
    var name: String = ""
    var status: String = ""
    var description: String = ""
    var dateExist: String = ""
    var imageUrl: String = ""
    var donationReceivedString: String = ""
    var totalRequiredString: String = ""
    var userId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity_update_activity)

        val activityId = intent.getStringExtra("activityId")

        // Initialize EditText and Button views
        nameText = findViewById(R.id.name)
        descriptionText = findViewById(R.id.description)
        totalRequireText = findViewById(R.id.totalRequire)
        calendarView = findViewById(R.id.calendarView)
        imageView = findViewById(R.id.imageView)
        userIdText = findViewById(R.id.userIdTextView)
        activityIdText = findViewById(R.id.activityIdTextView)
        totalReceiveText = findViewById(R.id.totalReceive)
        statusText = findViewById(R.id.statusTextView)
        updateButton = findViewById(R.id.updatebtn)



        val db = FirebaseFirestore.getInstance()
        val activityCollection = db.collection("activity")

        if (activityId != null) {
            activityCollection.document(activityId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Document exists, retrieve data

                        id = documentSnapshot.reference.id
                        name = documentSnapshot.getString("name") ?: ""
                        status = documentSnapshot.getString("status") ?: ""
                        description = documentSnapshot.getString("description") ?: ""
                        dateExist =documentSnapshot.getString("date") ?: ""
                        donationReceivedString = documentSnapshot.getString("donationReceived") ?: ""
                        imageUrl = documentSnapshot.getString("imageUrl") ?: ""

                        totalRequiredString = documentSnapshot.getString("totalRequired") ?: ""

                        userId = documentSnapshot.getString("userId") ?: ""


//                        // Now you have the existing activity data, you can populate your UI elements
//                        // Populate EditText fields with existing data
                        userIdText.setText(userId)
                        activityIdText.setText(id)
                        nameText.setText(name)
                        statusText.setText(status)
                        descriptionText.setText(description)
                        var into: Any = Glide.with(this)
                            .load(imageUrl)
                            .into(imageView)


                        dateExist?.let { date ->
                            // Assuming date is in the format "yyyy-MM-dd"
                            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                            val dateObj = dateFormat.parse(date)

                            if (dateObj != null) {
                                // Convert the date object to milliseconds
                                val dateMilliseconds = dateObj.time

                                // Set the CalendarView date to the converted date
                                calendarView.date = dateMilliseconds
                            }
                        }
                        totalRequireText.setText(totalRequiredString)
                        totalReceiveText.setText(donationReceivedString)
                    } else {
                        // Document does not exist
                        // Handle the case where the activity with the given ID does not exist
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(ContentValues.TAG, "Error fetching Firestore data: $exception")
                }

//
            // Set a click listener for the Send button
            updateButton.setOnClickListener(View.OnClickListener {
                // Retrieve user input from EditText fields
                val name = nameText.text.toString()
                val description = descriptionText.text.toString()
                val totalRequire = totalRequireText.text.toString()

                // Check for input validation
                if (name.isEmpty() || description.isEmpty() || totalRequire.isEmpty() || calendarView.date == 0L) {
                    Toast.makeText(this, "Please fill in all fields and select a date", Toast.LENGTH_SHORT).show()
                    return@OnClickListener // Exit the function if validation fails
                }

                // Validate totalRequire as a numeric value
                try {
                    val totalRequireValue = totalRequire.toInt()
                    if (totalRequireValue <= 0) {
                        Toast.makeText(this, "Total Required must be a positive number", Toast.LENGTH_SHORT).show()
                        return@OnClickListener
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Total Required must be a valid number", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }

                // If all input is valid, proceed to update the Firestore document
                val selectedDateMilliseconds = calendarView.date
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                val date = dateFormat.format(Date(selectedDateMilliseconds))

                // Prepare the data to update the Firestore document
                val activityUpdateData = hashMapOf(
                    "name" to name,
                    "description" to description,
                    "date" to date,
                    "totalRequired" to totalRequire
                )

                // Update the Firestore document with the new data
                val activityId = intent.getStringExtra("activityId")
                if (activityId != null) {
                    db.collection("activity").document(activityId).update(activityUpdateData as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Activity updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update activity", Toast.LENGTH_SHORT).show()
                        }
                }
            })






        }


//            val userId= "U0004" //currentUser.uid
//
//
//            val activity = hashMapOf(
//                "name" to name,
//                "status" to status,
//                "description" to description, // Store the selected value, not the RadioGroup
//                "date" to date,
//                "totalRequired" to totalRequire,
//            )
//
//            // Reference a new document with a generated ID
//            val documentId = "U0200"
//
//            val num = 0
//
//            db.collection("activity").document(documentId).update(activity as Map<String, Any>)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "Successfully Create Food!", Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener{
//                    Toast.makeText(this, "Failed To Create Food!", Toast.LENGTH_SHORT).show()
//                }
//
//
//            /*val currentUser = FirebaseAuth.getInstance().currentUser
//            if (currentUser != null) {
//
//            } else {
//                // Handle the case where the user is not signed in
//                Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
//            }*/
//        })



    }
}