package com.example.assignment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.assignment.UserFragment.UserActivityFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class UserActivityCreateActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    private lateinit var nameText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var totalRequireText: EditText
    private lateinit var createButton: Button
    private lateinit var calendarView: CalendarView
    private lateinit var imageView: ImageView
    private var uri: Uri? = null
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity_create_activity)

        initializeViews()
        setupListeners()
        generateActivityId()
    }

    private fun initializeViews() {
        nameText = findViewById(R.id.name)
        descriptionText = findViewById(R.id.description)
        totalRequireText = findViewById(R.id.totalRequire)
        calendarView = findViewById(R.id.calendarView)
        imageView = findViewById(R.id.imageView)
        createButton = findViewById(R.id.btn)
    }

    private fun setupListeners() {
        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            it?.let {
                imageView.setImageURI(it)
                uri = it
            }
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Go back to the previous screen
        }

        imageView.setOnClickListener {
            galleryImage.launch("image/*")
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Adjust month as CalendarView months are zero-based
            selectedDate = String.format("%02d-%02d-%d", dayOfMonth, month + 1, year)
            Toast.makeText(this, "Selected date: $selectedDate", Toast.LENGTH_SHORT).show()
        }

        createButton.setOnClickListener {
            handleCreateButtonClick()
        }
    }

    private fun handleCreateButtonClick() {
        val name = nameText.text.toString()
        val status = "pending"
        val description = descriptionText.text.toString()
        val totalDonationReceived = "0"
        val totalRequire = totalRequireText.text.toString()

        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid

        if (name.isEmpty() || description.isEmpty() || totalRequire.isEmpty()) {
            showToast("Please fill in all fields")
            return
        }

        if (selectedDate.isNullOrEmpty()) {
            showToast("Please select a date")
            return
        }

        if (uri == null) {
            showToast("Please select an image")
            return
        }

        try {
            val totalRequireValue = totalRequire.toInt()
            if (totalRequireValue <= 0) {
                showToast("Total Required must be a positive number")
                return
            }
        } catch (e: NumberFormatException) {
            showToast("Total Required must be a valid number")
            return
        }

        if (userUid != null) {
            fetchUserAndUploadData(userUid, name, status, description, totalDonationReceived, totalRequire, selectedDate!!)
        }
    }

    private fun fetchUserAndUploadData(userUid: String, name: String, status: String, description: String,
                                       totalDonationReceived: String, totalRequire: String, selectedDate: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userCollection = firestore.collection("user")

        userCollection.whereEqualTo("id", userUid)
            .get()
            .addOnSuccessListener { userQuerySnapshot ->
                if (!userQuerySnapshot.isEmpty) {
                    val userDocument = userQuerySnapshot.documents[0]
                    val userId = userDocument.id

                    uploadImageAndSaveData(name, status, description, totalDonationReceived, totalRequire, userId, selectedDate)
                } else {
                    showToast("No user found with this UID.")
                }
            }
            .addOnFailureListener { exception ->
                showError("Error fetching user data: ${exception.message}")
            }
    }

    private fun uploadImageAndSaveData(name: String, status: String, description: String,
                                       totalDonationReceived: String, totalRequire: String, userId: String, date: String) {
        if (uri != null) {
            val imageFileName = "${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child("images/$imageFileName")

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
                                showToast("Activity added successfully")

                                // Notify the callback
                                openFragment(UserActivityFragment())
                            }
                            .addOnFailureListener { e ->
                                showError("Error adding document: ${e.message}")
                            }
                    }
                }
            }
                .addOnFailureListener { e ->
                    showError("Error uploading image: ${e.message}")
                }
        } else {
            showToast("Please select an image")
        }
    }

    private fun generateActivityId() {
        generateDocumentId(0, db.collection("activity")) { documentId ->
            intent.putExtra("activityId", documentId)
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
                showError("Error getting document: ${exception.message}")
            }
    }

    private fun openFragment(fragment: Fragment) {
        val intent = Intent(this, UserHomeActivity::class.java)

        // Optionally, pass data to the new activity (fragment)
        intent.putExtra("fragmentToOpen", "Activity")
        startActivity(intent)
        finish() // This will close the current activity
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        showToast("Error: $message")
    }
}
