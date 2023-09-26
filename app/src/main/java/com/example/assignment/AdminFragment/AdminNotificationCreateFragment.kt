package com.example.assignment.AdminFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.example.assignment.R
import com.example.assignment.UserFragment.UserNotificationFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID


class AdminNotificationCreateFragment : Fragment() {

    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.admin_fragment_notification_create, container, false)


        view.findViewById<Button>(R.id.adminAddNotificationBtn).setOnClickListener {

            val notificationTitle =
                view.findViewById<EditText>(R.id.notificationTitleText).text.toString()
            val notificationDetails =
                view.findViewById<EditText>(R.id.notificationDetails).text.toString()

            val currentUser = FirebaseAuth.getInstance().currentUser

//            if (currentUser != null) {
//                val userId = currentUser.uid
            val notification = hashMapOf(
                "title"       to notificationTitle,
                "description" to notificationDetails
            )

//                db.collection("feedback").document("").set(feedback)
            db.collection("notification").add(notification)
                .addOnSuccessListener {
                    //Toast.makeText(this, "Successfully Create Food!", Toast.LENGTH_SHORT).show()
                    openFragment(AdminNotificationViewFragment())
                }
                .addOnFailureListener {
                    //Toast.makeText(this, "Failed To Create Food!", Toast.LENGTH_SHORT).show()
                }


//            } else {
//                // Handle the case where the user is not signed in
//                //Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
//            }
        }
        return view
    }

    private fun openFragment(fragment : Fragment){
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.admin_fl_wrapper, fragment)
        fragmentTransaction.addToBackStack(null) // Optional, to allow back navigation
        fragmentTransaction.commit()
    }
}