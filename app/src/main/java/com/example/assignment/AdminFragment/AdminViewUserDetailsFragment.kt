package com.example.assignment.AdminFragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.assignment.Adapter.UserListAdapter
import com.example.assignment.Model.User
import com.example.assignment.R

class AdminViewUserDetailsFragment : Fragment() {
    private lateinit var imageView: ImageView
    private lateinit var nameText: TextView
    private lateinit var emailText: TextView
    private lateinit var contactText: TextView
    private lateinit var genderText: TextView
    private lateinit var passwordText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.admin_fragment_viewuser, container, false)

        val builder = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(requireActivity())

        view.findViewById<ImageView>(R.id.user_details_backButton).setOnClickListener{
            requireActivity().onBackPressed()
        }

        // Retrieve the user data from the arguments bundle
        val user = arguments?.getParcelable(UserListAdapter.ARG_NOTIFICATION) as User?

        imageView = view.findViewById(R.id.user_profile_img)
        nameText = view.findViewById(R.id.user_name)
        emailText = view.findViewById(R.id.user_email)
        contactText = view.findViewById(R.id.user_contactno)
        genderText = view.findViewById(R.id.user_gender)
        passwordText = view.findViewById(R.id.user_password)

        // Populate the user details fields with the existing data
        user?.let { user ->
            Glide.with(this)
                .load(user.imageUrl) // Assuming activity.imageUrl contains the image URL
                .into(imageView)
            nameText.text = user.name
            emailText.text = user.email
            contactText.text = user.contactNo
            genderText.text = user.gender
            passwordText.text = user.password
        }

        return view
    }
}