package com.example.assignment.Adapter

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.AdminFragment.AdminNotificationUpdateFragment
import com.example.assignment.Model.Notification
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore

class NotificationAdapter(private val context: Context, private val fragmentManager: FragmentManager,
                          public var notificationList: MutableList<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    companion object {
        const val ARG_NOTIFICATION = "notification"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView = itemView.findViewById(R.id.notificationIdView)
        val titleTextView: TextView = itemView.findViewById(R.id.notificationTitleView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.notificationDescriptionView)
        val editButton: Button = itemView.findViewById(R.id.editNotificationButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteNotificationButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_notification_list_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.idTextView.text = notification.id
        holder.titleTextView.text = notification.title
        holder.descriptionTextView.text = notification.description


        // Set click listeners for edit and delete buttons
        holder.editButton.setOnClickListener {
            // Handle edit button click here
            // You can open an edit dialog/fragment here
            val notificationToEdit = notificationList[position]

            // Create a new instance of the EditNotificationDialogFragment
            val editDialogFragment = AdminNotificationUpdateFragment()

            // Pass the notification data to the fragment
            val args = Bundle()
            args.putParcelable(ARG_NOTIFICATION, notificationToEdit)
            editDialogFragment.arguments = args

            // Show the edit dialog using the FragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.admin_fl_wrapper, editDialogFragment)
                .addToBackStack(null)
                .commit()

        }

        holder.deleteButton.setOnClickListener {
            val positionToDelete = holder.adapterPosition
            val notificationToDelete = notificationList[positionToDelete]

            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Delete Notification")
            alertDialogBuilder.setMessage("Are you sure you want to delete this notification?")
            alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
                // Remove the notification from the list locally
                notificationList.remove(notificationToDelete)
                notifyItemRemoved(positionToDelete)

                // Delete the notification from Firestore
                val db = FirebaseFirestore.getInstance()
                val notificationCollection = db.collection("notification")

                // Use the correct document ID to delete the specific notification in Firestore
                val notificationIdToDelete = notificationToDelete.id // Assuming id is the correct document ID
                notificationCollection.document(notificationIdToDelete)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Notification deleted from Firestore")
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error deleting notification from Firestore: $exception")
                    }
            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialogBuilder.show()
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}


