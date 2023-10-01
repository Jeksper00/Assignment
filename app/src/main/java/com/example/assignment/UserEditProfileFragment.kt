package com.example.assignment

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
import com.example.assignment.R
import com.example.assignment.UserFragment.UserProfileFragment
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class UserEditProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var userNameEditText: EditText
    private lateinit var userEmailEditText: TextView
    private lateinit var userContactEditText: EditText
    private lateinit var userGenderEditText: EditText
    private lateinit var saveChangesButton: Button
    private lateinit var userProfileImage: ImageView
    private lateinit var editIcon: ImageView
    private var uri: Uri? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_fragment_edit_profile, container, false)
        var username = ""
        var email = ""
        var contact = ""
        var gender = ""
        var profileImageURL = ""

        firestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()

        userNameEditText = view.findViewById(R.id.userName)
        userEmailEditText = view.findViewById(R.id.userEmailAdd)
        userContactEditText = view.findViewById(R.id.userContact)
        userGenderEditText = view.findViewById(R.id.userGender)
        saveChangesButton = view.findViewById(R.id.saveButton)
        userProfileImage = view.findViewById(R.id.imageUserProfile)
        editIcon = view.findViewById(R.id.iconEdit)

        // Retrieve the userId from the arguments
        var userId = arguments?.getString("userid")

        if (userId != null) {
            // Retrieve user data from Firestore
            firestore.collection("user")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Get user data from Firestore
                        username = documentSnapshot.getString("name").toString()
                        email = documentSnapshot.getString("email").toString()
                        contact = documentSnapshot.getString("contact").toString()
                        gender = documentSnapshot.getString("gender").toString()
                        profileImageURL = documentSnapshot.getString("profileImageURL").toString()

                        // Set retrieved data to the EditTexts
                        userNameEditText.setText(username)
                        userEmailEditText.setText(email)
                        userContactEditText.setText(contact)
                        userGenderEditText.setText(gender)

                        var into: Any = Glide.with(this)
                            .load(profileImageURL)
                            .into(userProfileImage)
                    }
                }
                .addOnFailureListener { e ->
                    // Handle any errors that may occur during data retrieval
                }

            val galleryImage = registerForActivityResult(
                ActivityResultContracts.GetContent()
            ) {
                it?.let {
                    userProfileImage.setImageURI(it)
                    uri = it
                }
            }

            editIcon.setOnClickListener {
                galleryImage.launch("image/*")
            }
        }

        saveChangesButton.setOnClickListener {
            val userUid = auth.uid
            val newName = userNameEditText.text.toString()
            val newContact = userContactEditText.text.toString()
            val newGender = userGenderEditText.text.toString()

            // Upload image and save data
            uploadImageAndSaveData(userUid, newName,email, newContact, newGender)
        }

        return view
    }

    private fun uploadImageAndSaveData(userUid: String?, name: String, email: String, contact: String, gender: String) {
        if (uri != null && userUid != null) {
            val imageFileName = "${userUid}_${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child("profile/$imageFileName")

            val uploadTask = imageRef.putFile(uri!!)

            uploadTask.addOnSuccessListener { _ ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()

                    val userData = hashMapOf(
                        "id" to userUid,
                        "name" to name,
                        "email" to email,
                        "contact" to contact,
                        "gender" to gender,
                        "profileImageURL" to imageUrl
                    )

                    // Retrieve the userId from the arguments
                    var userId = arguments?.getString("userid").toString()

                    if (userUid != null) {
                        firestore.collection("user").document(userId).set(userData)
                            .addOnSuccessListener {
                                showToast("Profile update successfully")
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
