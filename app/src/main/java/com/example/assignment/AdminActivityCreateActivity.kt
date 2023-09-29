package com.example.assignment

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.assignment.AdminFragment.AdminActivityFragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class AdminActivityCreateActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private var storageRef = Firebase.storage

    private lateinit var nameText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var totalRequireText: EditText
    private lateinit var createButton: Button
    private lateinit var calendarView: CalendarView
    private lateinit var imageView: ImageView
    private  var uri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity_create_activity)

        nameText = findViewById(R.id.name)
        descriptionText = findViewById(R.id.description)
        totalRequireText = findViewById(R.id.totalRequire)
        calendarView = findViewById(R.id.calendarView)
        imageView = findViewById(R.id.imageView)
        createButton = findViewById(R.id.btn)

        // Generate activity ID immediately
        generateDocumentId(0, db.collection("activity")) { documentId ->
            intent.putExtra("activityId", documentId)
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

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Go back to the previous screen
        }

        imageView.setOnClickListener {
            galleryImage.launch("image/*")
        }

        var date = ""
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Adjust month as CalendarView months are zero-based
            date = String.format("%02d-%02d-%d", dayOfMonth, month + 1, year)
            Toast.makeText(this, "Selected date: $date", Toast.LENGTH_SHORT).show()


        }

        createButton.setOnClickListener {
            val name = nameText.text.toString()
            val status = "admin"
            val description = descriptionText.text.toString()
            val totalDonationReceived = "0"
            val totalRequire = totalRequireText.text.toString()


            val userId = "U0003"

            if (name.isEmpty() || description.isEmpty() || totalRequire.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (uri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val totalRequireValue = totalRequire.toInt()
                if (totalRequireValue <= 0) {
                    Toast.makeText(this, "Total Required must be a positive number", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Total Required must be a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImageAndSaveData(
                name,
                status,
                description,
                date,
                totalDonationReceived,
                totalRequire,
                userId
            )



        }
    }



    private fun generateDocumentId(num: Int, collectionRef: CollectionReference, callback: (String) -> Unit) {
        val formattedCounter = String.format("%04d", num)
        val documentIdToCheck = "A$formattedCounter"

        collectionRef.document(documentIdToCheck)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    generateDocumentId(num + 1, collectionRef, callback)
                } else {
                    callback(documentIdToCheck)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error getting document: $exception")
            }
    }

    private fun uploadImageAndSaveData(
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
            val imageRef = storageRef.reference.child("images/$imageFileName")


            val uploadTask = imageRef.putFile(uri!!)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()

                    val activities = hashMapOf(
                        "name" to name,
                        "status" to status,
                        "description" to description,
                        "imageUrl" to imageUrl,
                        "date" to date,
                        "totalDonationReceived" to totalDonationReceived,
                        "totalRequired" to totalRequire,
                        "userid" to userId
                    )

                    val activityId = intent.getStringExtra("activityId")

                    if (activityId != null) {
                        db.collection("activity").document(activityId).set(activities)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Activity added successfully", Toast.LENGTH_SHORT).show()

                                val activity = Activity(
                                    activityId,
                                    name,
                                    status,
                                    description,
                                    date,
                                    totalDonationReceived.toDoubleOrNull() ?: 0.0,
                                    totalRequire.toDoubleOrNull() ?: 0.0,
                                    userId,
                                    imageUrl
                                )

                                // Notify the callback
                                openFragment(AdminActivityFragment())
                            }
                            .addOnFailureListener { e ->
                                Log.e(ContentValues.TAG, "Error adding document", e)
                            }
                    }
                }
            }
                .addOnFailureListener { e ->
                    Log.e(ContentValues.TAG, "Error uploading image", e)
                }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }
    private fun openFragment(fragment : Fragment){
        val intent = Intent(this, AdminHomeActivity::class.java)

        // Optionally, pass data to the new activity (fragment)
        intent.putExtra("fragmentToOpen", "Activity")
        startActivity(intent)
        finish() // This will close the current activity
    }
}