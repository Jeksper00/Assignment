package com.example.assignment.Adapter

import android.app.AlertDialog
import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Model.Feedback
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.assignment.Model.Notification


class FeedbackAdapter(private val context: Context, private val fragmentManager: FragmentManager,
                      public var feedbackList: MutableList<Feedback>) :
    RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gmailTextView: TextView = itemView.findViewById(R.id.feedbackGmail)
        val feedbackTextView: TextView = itemView.findViewById(R.id.feedbackText)
        val deleteFeedbackButton : Button = itemView.findViewById(R.id.admin_delete_feedback)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_feedback_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.gmailTextView.text = feedback.gmail
        holder.feedbackTextView.text = feedback.feedback

        holder.deleteFeedbackButton.setOnClickListener{
            val positionToDelete = holder.adapterPosition
            val feedbackToDelete = feedbackList[positionToDelete]

            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Delete Notification")
            alertDialogBuilder.setMessage("Are you sure you want to delete this notification?")
            alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
                // Remove the notification from the list locally
                feedbackList.remove(feedbackToDelete)
                notifyItemRemoved(positionToDelete)

                // Delete the notification from Firestore
                val db = FirebaseFirestore.getInstance()
                val feedbackCollection = db.collection("feedback")

                // Use the correct document ID to delete the specific notification in Firestore
                val feedbackIdToDelete = feedbackToDelete.id // Assuming id is the correct document ID
                feedbackCollection.document(feedbackIdToDelete)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Feedback deleted from Firestore")

                        // Show a success message in a dialog
                        val successDialogBuilder = AlertDialog.Builder(context)
                        successDialogBuilder.setTitle("Success")
                        successDialogBuilder.setMessage("Feedback deleted successfully.")
                        successDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        successDialogBuilder.show()
                    }
                    .addOnFailureListener { exception ->
                        Log.e(ContentValues.TAG, "Error deleting feedback from Firestore: $exception")

                        // Show an error message in a dialog if deletion fails
                        val errorDialogBuilder = AlertDialog.Builder(context)
                        errorDialogBuilder.setTitle("Error")
                        errorDialogBuilder.setMessage("Error deleting feedback: $exception")
                        errorDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        errorDialogBuilder.show()
                    }
            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialogBuilder.show()
        }
    }

    override fun getItemCount(): Int {
        return feedbackList.size
    }
}