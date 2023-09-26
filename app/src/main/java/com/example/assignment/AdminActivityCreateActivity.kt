package com.example.assignment
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.collections.hashMapOf


class AdminActivityCreateActivity : AppCompatActivity() {

    private var db = Firebase.firestore

    private var storageRef = Firebase.storage


    private lateinit var nameText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var totalRequireText: EditText
    private lateinit var createButton: Button
    private lateinit var calendarView: CalendarView
    private lateinit var imageView: ImageView
    private lateinit var uri: Uri
    private lateinit var imageButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity_create_activity) // Replace with your layout XML file
        // Initialize EditText and Button views
        nameText = findViewById(R.id.name)
        descriptionText = findViewById(R.id.description)
        totalRequireText = findViewById(R.id.totalRequire)
        calendarView = findViewById(R.id.calendarView)
        imageView = findViewById(R.id.imageView)
        imageButton = findViewById(R.id.imageButton)
        createButton = findViewById(R.id.btn)

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                imageView.setImageURI(it)
                if (it != null) {
                    uri=it
                }
            }
        )

        imageButton.setOnClickListener({
            galleryImage.launch("image/*")

        })

        // Set a click listener for the Send button
        createButton.setOnClickListener(View.OnClickListener {


            //Retrieve user input from EditText fields
            // Retrieve user input from EditText fields
            val name = nameText.text.toString()
            val status = "pending"
            val description = descriptionText.text.toString()
            val totalDonationReceived = "0"
            val totalRequire = totalRequireText.text.toString()



            // If all input is valid, proceed to upload the image and save data
            val selectedDateMilliseconds = calendarView.date
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val date = dateFormat.format(Date(selectedDateMilliseconds))
            val userId = "U0003" //currentUser.uid


            // Check for input validation
            if (name.isEmpty() || description.isEmpty() || totalRequire.isEmpty() || uri == null) {
                Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show()
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

            if (uri != null) {
                // Upload the selected image to Firebase Storage and save the URL in Firestore
                uploadImageAndSaveData(
                    name,
                    status,
                    description,
                    date,
                    totalDonationReceived,
                    totalRequire,
                    userId
                )
            } else {
                // No image selected, handle accordingly
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }


        })

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
        // Check if an image is selected
        if (uri != null) {
            // Generate a unique filename for the image in Firebase Storage
            val imageFileName = "${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.reference.child("images/$imageFileName")

            // Upload the selected image to Firebase Storage
            val uploadTask = imageRef.putFile(uri!!)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully
                // Now, retrieve the download URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()

                    // Create an object with data to be saved in Firestore
                    val activity = hashMapOf(
                        "name" to name,
                        "status" to status,
                        "description" to description, // Store the selected value, not the RadioGrou
                        "imageUrl" to imageUrl,
                        "date" to date,
                        "totalDonationReceived" to totalDonationReceived,
                        "totalRequired" to totalRequire,
                        "userid" to userId
                    )




                    val activityId = intent.getStringExtra("activityId")


                    // Add this data to Firestore
                    if (activityId != null) {
                        db.collection("activity").document(activityId).set(activity)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(this, "Activity added successfully", Toast.LENGTH_SHORT).show()
                                finish() // Finish the activity or navigate to another screen
                            }
                            .addOnFailureListener { e ->
                                Log.e(ContentValues.TAG, "Error adding document", e)
                                // Handle the error as needed
                            }
                    }
                }
            }
                .addOnFailureListener { e ->
                    Log.e(ContentValues.TAG, "Error uploading image", e)
                    // Handle the error as needed
                }
        } else {
            // No image selected, handle accordingly
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }





}
