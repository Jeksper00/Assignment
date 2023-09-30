package com.example.assignment.Adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.AdminFragment.AdminHomeFragment
import com.example.assignment.Model.Activity
import com.example.assignment.R
import com.example.assignment.UserFragment.UserHomeActivityViewFragment
import com.google.firebase.firestore.FirebaseFirestore


class ActivityAdapter3 (private val context: Context, private val fragmentManager: FragmentManager,
                        public var activityList: MutableList<Activity>) :
    RecyclerView.Adapter<ActivityAdapter3.ViewHolder>() {

    companion object {
        const val ARG_NOTIFICATION = "notification"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView            = itemView.findViewById(R.id.userListActivityIdView)
        val imageView: ImageView            = itemView.findViewById(R.id.user_home_activity_imageView2)
        val activityNameTextView: TextView  = itemView.findViewById(R.id.userListActivityNameView)
        val statusTextView: TextView        = itemView.findViewById(R.id.userListActivityStatusView)
        val descriptionTextView: TextView   = itemView.findViewById(R.id.userListActivityDescriptionView)
        val dateTextView: TextView          = itemView.findViewById(R.id.userListActivityDateView)
        val totalDonationView: TextView     = itemView.findViewById(R.id.userListActivityDonationReceivedView)
        val totalRequiredTextView: TextView = itemView.findViewById(R.id.userListActivityTtlRequiredView)
        val userIdTextView: TextView        = itemView.findViewById(R.id.userListActivityCreatorView)
        val approveActivityButton: Button   = itemView.findViewById(R.id.userListActivityAproveButton)
        val rejectActivityButton: Button    = itemView.findViewById(R.id.userListActivityRejectButton)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_adminhome_activitylist, parent, false)
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

        holder.approveActivityButton.setOnClickListener {

            val db = FirebaseFirestore.getInstance()
            val activityCollection = db.collection("activity")
            holder.idTextView.text = activity.id
            activityCollection.document(activity.id)
                .update("status", "approve")

            val toast = Toast.makeText(context, "Approve Successful", Toast.LENGTH_SHORT)
            toast.show()

            fragmentManager.beginTransaction()
                .replace(R.id.admin_fl_wrapper, AdminHomeFragment())
                .addToBackStack(null)
                .commit()
        }
        holder.rejectActivityButton.setOnClickListener {

            val db = FirebaseFirestore.getInstance()
            val activityCollection = db.collection("activity")
            holder.idTextView.text = activity.id
            activityCollection.document(activity.id)
                .update("status", "reject")

            val toast = Toast.makeText(context, "Reject Successful", Toast.LENGTH_SHORT)
            toast.show()

            fragmentManager.beginTransaction()
                .replace(R.id.admin_fl_wrapper, AdminHomeFragment())
                .addToBackStack(null)
                .commit()
        }


    }

    override fun getItemCount(): Int {
        return activityList.size
    }

}