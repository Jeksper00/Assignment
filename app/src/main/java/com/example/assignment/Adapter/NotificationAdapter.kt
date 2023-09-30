package com.example.assignment.Adapter

import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.AdminFragment.AdminNotificationUpdateFragment
import com.example.assignment.Model.Notification
import com.example.assignment.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

    class NotificationAdapter(private val context: Context, private val fragmentManager: FragmentManager,
                          public var notificationList: MutableList<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    companion object {
        const val ARG_NOTIFICATION = "notification"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView          = itemView.findViewById(R.id.notificationIdView)
        val titleTextView: TextView       = itemView.findViewById(R.id.admin_notificationTitleView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.admin_notificationDescriptionView)
        val dateTextView: TextView        = itemView.findViewById(R.id.admin_notificationDateView)
        val editButton: Button            = itemView.findViewById(R.id.editNotificationButton)
        val deleteButton: Button          = itemView.findViewById(R.id.deleteNotificationButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_notification_list_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification                = notificationList[position]
        holder.idTextView.text          = notification.id
        holder.titleTextView.text       = notification.title
        holder.descriptionTextView.text = notification.description
        holder.dateTextView.text        = notification.date


        holder.editButton.setOnClickListener {

            val notificationToEdit = notificationList[position]
            val editDialogFragment = AdminNotificationUpdateFragment()

            val args = Bundle()
            args.putParcelable(ARG_NOTIFICATION, notificationToEdit)
            editDialogFragment.arguments = args

            fragmentManager.beginTransaction()
                .replace(R.id.admin_fl_wrapper, editDialogFragment)
                .addToBackStack(null)
                .commit()

        }

        holder.deleteButton.setOnClickListener {
            val positionToDelete = holder.adapterPosition
            val notificationToDelete = notificationList[positionToDelete]

            // Show a confirmation dialog before proceeding
            val confirmDialogBuilder = AlertDialog.Builder(context)
            confirmDialogBuilder.setTitle("Confirm Delete")
            confirmDialogBuilder.setMessage("Are you sure you want to delete this notification?")
            confirmDialogBuilder.setPositiveButton("Yes") { _, _ ->

                // User confirmed deletion
                // Create a Snackbar for the delete confirmation
                val coordinatorLayout = (context as AppCompatActivity).findViewById<View>(R.id.admin_notificationView_frame)
                val snackbar = Snackbar.make(
                    coordinatorLayout,
                    "Notification deleted successfully.",
                    Snackbar.LENGTH_LONG
                )

                // Add an action to undo the deletion
                snackbar.setAction("Undo") {
                    // Restore the deleted notification to the list
                    notificationList.add(positionToDelete, notificationToDelete)
                    notifyItemInserted(positionToDelete)
                }

                // Add an action to dismiss the Snackbar
                snackbar.setActionTextColor(context.resources.getColor(R.color.blue))
                snackbar.show()

                // Delay the deletion of the notification in Firestore until after the Snackbar duration
                val db = FirebaseFirestore.getInstance()
                val notificationCollection = db.collection("notification")

                // Use the correct document ID to delete the specific notification in Firestore
                val notificationIdToDelete = notificationToDelete.id // Assuming id is the correct document ID

                // Add a callback to detect when the Snackbar is dismissed
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)

                        // Check the event to see if the Snackbar was dismissed
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            // Snackbar was dismissed automatically (not due to user action)

                            // Delete the notification from Firestore
                            notificationCollection.document(notificationIdToDelete)
                                .delete()
                                .addOnFailureListener { exception ->
                                    Log.e(ContentValues.TAG, "Error deleting notification from Firestore: $exception")
                                }
                        }
                    }
                })

                // Remove the notification from the list locally
                notificationList.remove(notificationToDelete)
                notifyItemRemoved(positionToDelete)
            }

            confirmDialogBuilder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            confirmDialogBuilder.show()
        }
    }


    override fun getItemCount(): Int {
        return notificationList.size
    }
}


