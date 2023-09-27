package com.example.assignment.UserFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.assignment.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class UserFeedbackInputFragment : Fragment() {

    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.user_fragment_feedback_input, container, false)

        view.findViewById<ImageView>(R.id.user_feedbackInput_backButton).setOnClickListener{
            requireActivity().onBackPressed()
        }

        view.findViewById<Button>(R.id.userFeedbackSubmitBtn).setOnClickListener {

            val usergmail = view.findViewById<EditText>(R.id.userFeedbackGmail).text.toString()
            val details = view.findViewById<EditText>(R.id.user_feedback_text).text.toString()

            val currentUser = FirebaseAuth.getInstance().currentUser

//            if (currentUser != null) {
//                val userId = currentUser.uid
                val feedback = hashMapOf(
                    "gmail" to usergmail,
                    "feedback" to details
                )

//                db.collection("feedback").document("").set(feedback)
                db.collection("feedback").add(feedback)
                    .addOnSuccessListener {
                        showToast("Successfully Create Food!")
                        requireActivity().onBackPressed()
                    }
                    .addOnFailureListener {
                        //Toast.makeText(this, "Failed To Create Food!", Toast.LENGTH_SHORT).show()
                    }

                    //openFragment(UserNotificationFragment())

//            } else {
//                // Handle the case where the user is not signed in
//                //Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
//            }

        }
        return view
    }

    // Function to display a Toast message
    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFragment(fragment : Fragment){
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.user_fl_wrapper, fragment)
        fragmentTransaction.addToBackStack(null) // Optional, to allow back navigation
        fragmentTransaction.commit()
    }

}