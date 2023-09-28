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
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import io.grpc.okhttp.internal.framed.FrameReader


class FeedbackAdapter(private val context: Context, private val fragmentManager: FragmentManager,
                      public var feedbackList: MutableList<Feedback>) :
    RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gmailTextView: TextView       = itemView.findViewById(R.id.feedbackGmail)
        val feedbackTextView: TextView    = itemView.findViewById(R.id.feedbackText)
        val deleteFeedbackButton : Button = itemView.findViewById(R.id.admin_delete_feedback)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_feedback_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedback                 = feedbackList[position]
        holder.gmailTextView.text    = feedback.gmail
        holder.feedbackTextView.text = feedback.feedback

        holder.deleteFeedbackButton.setOnClickListener {
            val positionToDelete = holder.adapterPosition
            val feedbackToDelete = feedbackList[positionToDelete]

            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Delete Feedback")
            alertDialogBuilder.setMessage("Are you sure you want to delete this feedback?")
            alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
                // Remove the feedback from the list locally
                feedbackList.remove(feedbackToDelete)
                notifyItemRemoved(positionToDelete)

                // Create a Snackbar for the delete confirmation
                val coordinatorLayout =
                    (context as AppCompatActivity).findViewById<View>(R.id.admin_feedbackView_frame)
                val snackbar = Snackbar.make(
                    coordinatorLayout,
                    "Feedback deleted successfully.",
                    Snackbar.LENGTH_LONG
                )

                // Add an action to undo the deletion
                snackbar.setAction("Undo") {
                    // Restore the deleted feedback to the list
                    feedbackList.add(positionToDelete, feedbackToDelete)
                    notifyItemInserted(positionToDelete)
                }

                // Add an action to dismiss the Snackbar
                snackbar.setActionTextColor(context.resources.getColor(R.color.blue))
                snackbar.show()

                // Delay the deletion of feedback in Firestore until after the Snackbar duration
                val db = FirebaseFirestore.getInstance()
                val feedbackCollection = db.collection("feedback")

                // Use the correct document ID to delete the specific feedback in Firestore
                val feedbackIdToDelete = feedbackToDelete.id // Assuming id is the correct document ID

                // Add a callback to detect when the Snackbar is dismissed
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)

                        // Check the event to see if the Snackbar was dismissed
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            // Snackbar was dismissed automatically (not due to user action)

                            // Now you can perform actions when the Snackbar disappears
                            // Delete the feedback from Firestore, for example
                            val db = FirebaseFirestore.getInstance()
                            val feedbackCollection = db.collection("feedback")

                            // Use the correct document ID to delete the specific feedback in Firestore
                            val feedbackIdToDelete = feedbackToDelete.id // Assuming id is the correct document ID

                            feedbackCollection.document(feedbackIdToDelete)
                                .delete()
                                .addOnFailureListener { exception ->
                                    Log.e(ContentValues.TAG, "Error deleting feedback from Firestore: $exception")
                                }
                        }
                    }
                })
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