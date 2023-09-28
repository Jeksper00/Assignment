// AdminActivityCreateActivity.kt

package com.example.assignment

import android.content.ContentValues
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
import com.example.assignment.AdminFragment.AdminActivityFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
    private lateinit var uri: Uri
    private lateinit var imageButton: Button

    private var activityCreationCallback: ActivityCreationCallback? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity_create_activity)

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
                    uri = it
                }
                else{
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                }
            }
        )
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // 返回到前一个界面
        }

        imageButton.setOnClickListener {
            galleryImage.launch("image/*")
        }

        createButton.setOnClickListener {
            val name = nameText.text.toString()
            val status = "admin"
            val description = descriptionText.text.toString()
            val totalDonationReceived = "0"
            val totalRequire = totalRequireText.text.toString()

            val selectedDateMilliseconds = calendarView.date
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val date = dateFormat.format(Date(selectedDateMilliseconds))
            val userId = "U0003"

            if (name.isEmpty() || description.isEmpty() || totalRequire.isEmpty() || uri == null) {
                Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show()
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

            if (uri != null) {
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
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setActivityCreationCallback(callback: AdminActivityFragment) {
        activityCreationCallback = callback
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
        if (uri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }
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

                                activityCreationCallback?.onActivityCreated(activity)
                                finish()
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

    interface ActivityCreationCallback {
        fun onActivityCreated(activity: Activity)
    }
}
