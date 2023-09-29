package com.example.assignment.Adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.AdminFragment.AdminUpdateUserDetailsFragment
import com.example.assignment.AdminFragment.AdminViewUserDetailsFragment
import com.example.assignment.Model.User
import com.example.assignment.R


class UserListAdapter(private val context: Context, private val fragmentManager: FragmentManager,
                      public var userList: MutableList<User>) :
        RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

        companion object {
                const val ARG_NOTIFICATION = "notification"
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val viewButton: Button = itemView.findViewById(R.id.view_btn)
                val editButton: Button = itemView.findViewById(R.id.edit_btn)
                val deleteButton: Button = itemView.findViewById(R.id.delete_btn)
                val profileImgView: ImageView = itemView.findViewById(R.id.profile_img)
                val usernameTextView: TextView = itemView.findViewById(R.id.username)
                val emailTextView: TextView = itemView.findViewById(R.id.email)
                val contactNoTextView: TextView = itemView.findViewById(R.id.contact_no)
                val genderNoTextView: TextView = itemView.findViewById(R.id.gender)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_user_list, parent, false)
                return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val user = userList[position]

                // Use Glide to load and display the image from the URL
                Glide.with(holder.itemView)
                        .load(user.imageUrl) // Assuming activity.imageUrl contains the image URL
                        .into(holder.profileImgView)
                holder.usernameTextView.text = user.name
                holder.emailTextView.text = user.email
                holder.contactNoTextView.text = user.contactNo
                holder.genderNoTextView.text = user.gender

                // Set click listeners for buttons
                holder.viewButton.setOnClickListener {
                        val userDetails = userList[position]

                        // Create a new instance of the EditNotificationDialogFragment
                        val userViewFragment = AdminViewUserDetailsFragment()

                        // Pass the notification data to the fragment
                        val args = Bundle()
                        args.putParcelable(ARG_NOTIFICATION, userDetails)
                        userViewFragment.arguments = args

                        // Show the edit dialog using the FragmentManager
                        fragmentManager.beginTransaction()
                                .replace(R.id.admin_fl_wrapper, userViewFragment)
                                .addToBackStack(null)
                                .commit()
                }

                // Set click listeners for buttons
                holder.editButton.setOnClickListener {
                        val userToEdit = userList[position]

                        // Create a new instance of the EditNotificationDialogFragment
                        val userViewFragment = AdminUpdateUserDetailsFragment()

                        // Pass the notification data to the fragment
                        val args = Bundle()
                        args.putParcelable(ARG_NOTIFICATION, userToEdit)
                        userViewFragment.arguments = args

                        // Show the edit dialog using the FragmentManager
                        fragmentManager.beginTransaction()
                                .replace(R.id.admin_fl_wrapper, userViewFragment)
                                .addToBackStack(null)
                                .commit()
                }
        }

        override fun getItemCount(): Int {
                return userList.size
        }

}