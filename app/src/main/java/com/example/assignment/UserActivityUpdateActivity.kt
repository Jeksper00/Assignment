package com.example.assignment

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
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
import java.util.UUID

class UserActivityUpdateActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage.reference

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
    private lateinit var imageButton: Button
    private var uri: Uri? = null
    private var uploadImageUrl: String = ""

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
        setContentView(R.layout.user_activity_update_activity)

        val activityId = intent.getStringExtra("activityId")

        // Initialize EditText and Button views
        nameText = findViewById(R.id.name)
        nameText.setText(activityId)
        descriptionText = findViewById(R.id.description)
        totalRequireText = findViewById(R.id.totalRequire)
        calendarView = findViewById(R.id.calendarView)
        imageView = findViewById(R.id.imageView)
        userIdText = findViewById(R.id.userIdTextView)
        activityIdText = findViewById(R.id.activityIdTextView)
        totalReceiveText = findViewById(R.id.totalReceive)
        statusText = findViewById(R.id.statusTextView)
        updateButton = findViewById(R.id.updatebtn)
        imageButton = findViewById(R.id.imageButton)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Return to the previous screen
        }

        val db = FirebaseFirestore.getInstance()
        val activityCollection = db.collection("activity")

        if (activityId != null) {
            activityCollection.document(activityId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        id = documentSnapshot.reference.id
                        name = documentSnapshot.getString("name") ?: ""
                        status = documentSnapshot.getString("status") ?: ""
                        description = documentSnapshot.getString("description") ?: ""
                        dateExist = documentSnapshot.getString("date") ?: ""
                        donationReceivedString = documentSnapshot.getString("donationReceived") ?: ""
                        imageUrl = documentSnapshot.getString("imageUrl") ?: ""
                        totalRequiredString = documentSnapshot.getString("totalRequired") ?: ""
                        userId = documentSnapshot.getString("userId") ?: ""

                        userIdText.text = userId
                        activityIdText.text = id
                        nameText.setText(name)
                        statusText.text = status
                        descriptionText.setText(description)
                        Glide.with(this)
                            .load(imageUrl)
                            .into(imageView)

                        dateExist?.let { date ->
                            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                            val dateObj = dateFormat.parse(date)

                            if (dateObj != null) {
                                val dateMilliseconds = dateObj.time
                                calendarView.date = dateMilliseconds
                            }
                        }

                        totalRequireText.setText(totalRequiredString)
                        totalReceiveText.text = donationReceivedString
                    } else {
                        // Handle the case where the activity with the given ID does not exist
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(ContentValues.TAG, "Error fetching Firestore data: $exception")
                }

            val galleryImage = registerForActivityResult(
                ActivityResultContracts.GetContent(),
                ActivityResultCallback {
                    imageView.setImageURI(it)
                    if (it != null) {
                        uri = it
                    }
                }
            )

            imageButton.setOnClickListener {
                galleryImage.launch("image/*")
            }

            var date = dateExist
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                date = String.format("%02d-%02d-%d", dayOfMonth, month + 1, year)
                Toast.makeText(this, "Selected date: $date", Toast.LENGTH_SHORT).show()
            }

            updateButton.setOnClickListener(View.OnClickListener {

                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Update Activity")
                alertDialogBuilder.setMessage("Are you sure you want to Update this activity?")
                alertDialogBuilder.setPositiveButton("Update") { _, _ ->
                    // Delete the activity from Firestore and Firebase Storage
                    val name = nameText.text.toString()
                    val description = descriptionText.text.toString()
                    val totalRequire = totalRequireText.text.toString()

                    if (name.isEmpty() || description.isEmpty() || totalRequire.isEmpty() || calendarView.date == 0L) {
                        Toast.makeText(this, "Please fill in all fields and select a date", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    try {
                        val totalRequireValue = totalRequire.toInt()
                        if (totalRequireValue <= 0) {
                            Toast.makeText(this, "Total Required must be a positive number", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Total Required must be a valid number", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    uploadImageAndUpdateData(name, status, description, date, "0", totalRequire, "U0003")
                }
                alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialogBuilder.show()

            })
        }
    }

    private fun uploadImageAndUpdateData(
        name: String,
        status: String,
        description: String,
        date: String,
        totalDonationReceived: String,
        totalRequire: String,
        userId: String
    ) {
        if (uri != null) {
            val imageFileName = "${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child("images/$imageFileName")

            val uploadTask = imageRef.putFile(uri!!)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                val storageReference = FirebaseStorage.getInstance().getReference("images/$imageUrl.jpg")


                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    uploadImageUrl = downloadUri.toString()
                    // Delete the image from Firebase Storage
                    storageReference.delete()

                    val activities = hashMapOf(
                        "name" to name,
                        "status"  to status,
                        "description" to description,
                        "imageUrl" to uploadImageUrl,
                        "date" to date,
                        "totalRequired" to totalRequire,
                        "userid" to userId
                    )

                    val activityId = intent.getStringExtra("activityId")
                    if (activityId != null) {
                        db.collection("activity").document(activityId)
                            .update(activities as Map<String, Any>)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Activity updated successfully", Toast.LENGTH_SHORT).show()
                                openFragment(UserActivityFragment())
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to update activity", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
                .addOnFailureListener { e ->
                    Log.e(ContentValues.TAG, "Error uploading image", e)
                }
        } else {
            // Prepare the data to update the Firestore document
            val activityUpdateData = hashMapOf(
                "name" to name,
                "status"  to status,
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
                        openFragment(UserActivityFragment())
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update activity", Toast.LENGTH_SHORT).show()
                    }
            }
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
