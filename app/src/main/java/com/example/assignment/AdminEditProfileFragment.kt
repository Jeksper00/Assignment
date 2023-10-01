package com.example.assignment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.UUID

class AdminEditProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var adminNameEditText: EditText
    private lateinit var adminEmailEditText: TextView
    private lateinit var saveChangesButton: Button
    private lateinit var deleteButton: Button
    private lateinit var adminProfileImage: ImageView
    private lateinit var editIcon: ImageView
    private var admin: FirebaseUser? = null
    private var uri: Uri? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.admin_fragment_edit_profile, container, false)
        var username = ""
        var email = ""
        var profileImageURL = ""

        firestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        admin = FirebaseAuth.getInstance().currentUser

        adminNameEditText = view.findViewById(R.id.adminUsername)
        adminEmailEditText = view.findViewById(R.id.adminEmailAddress)
        saveChangesButton = view.findViewById(R.id.saveChangesBtn)
        deleteButton = view.findViewById(R.id.deleteBtn)
        adminProfileImage = view.findViewById(R.id.adminProfileImage)
        editIcon = view.findViewById(R.id.adminEditButton)

        //Retrieve the adminId from the arguments
        var adminId = arguments?.getString("adminid")

        adminNameEditText.setText(adminId)
        if (adminId != null) {
            // Retrieve admin data from Firestore
            firestore.collection("admin")
                .document(adminId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        //  Get admin data from Firestore
                        username = documentSnapshot.getString("name").toString()
                        email = documentSnapshot.getString("email").toString()
                        profileImageURL = documentSnapshot.getString("profileImageURL").toString()

//                        // Set retrieved data to the EditTexts
                        adminNameEditText.setText(username)
                        adminEmailEditText.setText(email)

                        var into: Any = Glide.with(this)
                            .load(profileImageURL)
                            .into(adminProfileImage)
                    }
                }
                .addOnFailureListener { e ->
                    // Handle any errors that may occur during data retrieval
                }

            val galleryImage = registerForActivityResult(
                ActivityResultContracts.GetContent()
            ) {
                it?.let {
                    adminProfileImage.setImageURI(it)
                    uri = it
                }
            }

            editIcon.setOnClickListener {
                galleryImage.launch("image/*")
            }
        }

        saveChangesButton.setOnClickListener {
            val adminUid = auth.uid
            val newName = adminNameEditText.text.toString()

            // Upload image and save data
            uploadImageAndSaveData(adminUid, newName, email)
        }

        return view
    }

    private fun uploadImageAndSaveData(adminUid: String?, name: String, email: String) {
        if (uri != null && adminUid != null) {
            val imageFileName = "${adminUid}_${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child("profile/$imageFileName")

            val uploadTask = imageRef.putFile(uri!!)

            uploadTask.addOnSuccessListener { _ ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()

                    val adminData = hashMapOf(
                        "id" to adminUid,
                        "name" to name,
                        "email" to email,
                        "profileImageURL" to imageUrl
                    )

                    // Retrieve the userId from the arguments
                    var adminId = arguments?.getString("adminid").toString()

                    if (adminUid != null) {
                        firestore.collection("admin").document(adminId).set(adminData)
                            .addOnSuccessListener {
                                showToast("Profile Image added successfully")
                                return@addOnSuccessListener
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

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        showToast(message)
        }
}
