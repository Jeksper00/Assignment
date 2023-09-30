//package com.example.assignment
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import com.google.firebase.auth.FirebaseAuth
//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.android.gms.tasks.Task
//import com.example.assignment.R
//
//class AdminForgotPasswordFragment : Fragment() {
//
//    private lateinit var adminEmailEditText: EditText
//    private lateinit var sendButton: Button
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.admin_fragment_forgot_password, container, false)
//
//        adminEmailEditText = view.findViewById(R.id.adminEmailAdrs)
//        sendButton = view.findViewById(R.id.sendBtn)
//        auth = FirebaseAuth.getInstance()
//
//        sendButton.setOnClickListener {
//            sendPasswordResetEmail()
//        }
//
//        return view
//    }
//
//    private fun sendPasswordResetEmail() {
//        val email = adminEmailEditText.text.toString()
//
//        if (email.isEmpty()) {
//            Toast.makeText(context, "Please enter your email address.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        auth.sendPasswordResetEmail(email)
//            .addOnCompleteListener(requireActivity(), OnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(context, "Password reset email sent. Please check your email.", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(context, "Failed to send password reset email. Please try again later.", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
//}
