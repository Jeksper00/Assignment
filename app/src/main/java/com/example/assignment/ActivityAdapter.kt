package com.example.assignment





import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ActivityAdapter(
    private val context: Context,
    private val requireFragmentManager: FragmentManager,
    public var activityList: MutableList<Activity>
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
       val itemView =LayoutInflater.from(parent.context).inflate(R.layout.admin_activity_retrieve_list,parent,false)
        return ActivityViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return activityList.size
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val currentitem = activityList[position]
        holder.activityId.text = currentitem.activityid
        holder.name.text = currentitem.name
        holder.date.text = currentitem.date
        holder.status.text = currentitem.status
        holder.donationReceived.text = currentitem.totalDonationReceived.toString()
        holder.totalRequired.text = currentitem.totalRequired.toString()
        holder.description.text = currentitem.description
        holder.userId.text = currentitem.userId






        ///update delete

         //Set click listeners for edit and delete buttons
        holder.editButton.setOnClickListener {
//  Log.d(TAG, "Edit button clicked for activity ID: ${currentitem.activityId}")

            // Create an Intent to navigate to the target activity (AdminActivityUpdateActivity)
            val intent = Intent(context, AdminActivityUpdateActivity::class.java)



            intent.putExtra("activityId", currentitem.activityid)


            context.startActivity(intent)

        }

        holder.deleteButton.setOnClickListener {
            val positionToDelete = holder.adapterPosition
            val activityToDelete = activityList[positionToDelete]

            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Delete Notification")
            alertDialogBuilder.setMessage("Are you sure you want to delete this activity?")
            alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
                // Remove the notification from the list locally
                activityList.remove(activityToDelete)
                notifyItemRemoved(positionToDelete)

                // Delete the notification from Firestore
                val db = FirebaseFirestore.getInstance()
                val activityCollection = db.collection("activity")

                // Use the correct document ID to delete the specific notification in Firestore
                val activityIdToDelete = activityToDelete.activityid

                // Retrieve the image reference or URL from the activityToDelete object
                val imageUrlToDelete = activityToDelete.imageUrl

                // Delete the image from Firebase Storage
                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlToDelete)
                storageReference.delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Image deleted from Firebase Storage")
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error deleting image from Firebase Storage: $exception")
                    }

                // Delete the activity document from Firestore
                activityCollection.document(activityIdToDelete)
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

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val activityId: TextView = itemView.findViewById(R.id.activityIdTextView)

        val name: TextView = itemView.findViewById(R.id.nameTextView)
        val status: TextView = itemView.findViewById(R.id.statusTextView)
        val description: TextView = itemView.findViewById(R.id.descriptionTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val donationReceived: TextView = itemView.findViewById(R.id.donationReceivedTextView)
        val totalRequired: TextView = itemView.findViewById(R.id.totalRequiredTextView)
        val userId: TextView = itemView.findViewById(R.id.userIdTextView)
        val editButton: TextView = itemView.findViewById(R.id.editActivityButton)
        val deleteButton: TextView = itemView.findViewById(R.id.deleteActivityButton)


    }

}