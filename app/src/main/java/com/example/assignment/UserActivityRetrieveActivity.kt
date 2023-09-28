package com.example.assignment

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.assignment.UserFragment.UserActivityFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Locale

class UserActivityRetrieveActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage

    private lateinit var nameText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var totalRequireText: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var userIdText: TextView
    private lateinit var activityIdText: TextView
    private lateinit var statusText: TextView
    private lateinit var totalReceiveText: TextView
    private lateinit var imageView: ImageView


    private lateinit var editButton: Button
    private lateinit var deleteButton: Button


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
        setContentView(R.layout.user_activity_retrieve_activity)

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
        editButton = findViewById(R.id.editbtn)
        deleteButton = findViewById(R.id.deletebtn)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // 返回到前一个界面
        }

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
            editButton.setOnClickListener(View.OnClickListener {
                val intent = Intent(this, UserActivityUpdateActivity::class.java)

                intent.putExtra("activityId", activityId)

                this.startActivity(intent)

            })

            deleteButton.setOnClickListener(View.OnClickListener {

                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Delete Activity")
                alertDialogBuilder.setMessage("Are you sure you want to delete this activity?")
                alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
                    // Delete the activity from Firestore and Firebase Storage
                    deleteActivityFromFirestore(activityId)
                }
                alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialogBuilder.show()

            })

        }


    }


    private fun deleteActivityFromFirestore(activityId: String) {
        // Delete the activity document from Firestore
        val db = FirebaseFirestore.getInstance()
        val activityCollection = db.collection("activity")

        // Delete the activity from Firestore
        activityCollection.document(activityId)
            .delete()
            .addOnSuccessListener {
                // Activity deleted successfully from Firestore
                Log.d(ContentValues.TAG, "Activity deleted from Firestore")

                // Now delete the image from Firebase Storage
                deleteImageFromStorage(activityId)
                openFragment(UserActivityFragment())
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred while deleting from Firestore
                Log.e(ContentValues.TAG, "Error deleting activity from Firestore: $exception")
            }
    }

    private fun deleteImageFromStorage(activityId: String) {
        // Retrieve the image reference or URL from the activity object
        // Here, assuming you have the image path in Firebase Storage related to the activity
        val storageReference = FirebaseStorage.getInstance().getReference("images/$activityId.jpg")

        // Delete the image from Firebase Storage
        storageReference.delete()
            .addOnSuccessListener {
                // Image deleted successfully from Firebase Storage
                Log.d(ContentValues.TAG, "Image deleted from Firebase Storage")

            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred while deleting image from Firebase Storage
                Log.e(ContentValues.TAG, "Error deleting image from Firebase Storage: $exception")
            }
    }
    private fun openFragment(fragment : Fragment){
        val intent = Intent(this, UserHomeActivity::class.java)

        // Optionally, pass data to the new activity (fragment)
        intent.putExtra("fragmentToOpen", "Activity")
        startActivity(intent)
        finish() // This will close the current activity
    }

}