package com.example.assignment.Adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.Model.Activity
import com.example.assignment.R
import com.example.assignment.UserFragment.UserHomeActivityViewFragment

class ActivityAdapter2 (private val context: Context, private val fragmentManager: FragmentManager,
                        public var activityList: MutableList<Activity>) :
    RecyclerView.Adapter<ActivityAdapter2.ViewHolder>() {

    companion object {
        const val ARG_NOTIFICATION = "notification"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView            = itemView.findViewById(R.id.userListActivityIdView)
        val imageView: ImageView            = itemView.findViewById(R.id.user_home_activity_imageView)
        val activityNameTextView: TextView  = itemView.findViewById(R.id.userListActivityNameView)
        val statusTextView: TextView        = itemView.findViewById(R.id.userListActivityStatusView)
        val descriptionTextView: TextView   = itemView.findViewById(R.id.userListActivityDescriptionView)
        val dateTextView: TextView          = itemView.findViewById(R.id.userListActivityDateView)
        val totalDonationView: TextView     = itemView.findViewById(R.id.userListActivityDonationReceivedView)
        val totalRequiredTextView: TextView = itemView.findViewById(R.id.userListActivityTtlRequiredView)
        val userIdTextView: TextView        = itemView.findViewById(R.id.userListActivityCreatorView)
        val showActivityButton: Button      = itemView.findViewById(R.id.userListActivityDonateButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_activity_list_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activityList[position]
        holder.idTextView.text = activity.id
        // Use Glide to load and display the image from the URL
        Glide.with(holder.itemView)
            .load(activity.imageUrl) // Assuming activity.imageUrl contains the image URL
            .into(holder.imageView)
        holder.activityNameTextView.text  = activity.name
        holder.statusTextView.text        = activity.status
        holder.descriptionTextView.text   = activity.description
        holder.dateTextView.text          = activity.date
        holder.totalDonationView.text     = activity.totalDonationReceived
        holder.totalRequiredTextView.text = activity.totalRequired
        holder.userIdTextView.text        = activity.userId


        // Set click listeners for buttons
        holder.showActivityButton.setOnClickListener {
            // Handle button click here
            // You can open an edit dialog/fragment here
            val activityDetails = activityList[position]

            // Create a new instance of the EditNotificationDialogFragment
            val activityViewFragment = UserHomeActivityViewFragment()

            // Pass the notification data to the fragment
            val args = Bundle()
            args.putParcelable(ARG_NOTIFICATION, activityDetails)
            activityViewFragment.arguments = args

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            // Show the edit dialog using the FragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.user_fl_wrapper, activityViewFragment)
                .addToBackStack(null)
                .commit()
        }


    }

    override fun getItemCount(): Int {
        return activityList.size
    }

}