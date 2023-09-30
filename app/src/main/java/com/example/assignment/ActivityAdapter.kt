package com.example.assignment





import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

class ActivityAdapter(
    private val context: Context,
    private val requireFragmentManager: FragmentManager,
    public var activityList: MutableList<Activity>
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.admin_activity_retrieve_list, parent, false)
        return ActivityViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return activityList.size
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val currentitem = activityList[position]
        val donationReceived = currentitem.totalDonationReceived
        val totalRequired = currentitem.totalRequired
        val progress = (donationReceived.toFloat() / totalRequired.toFloat() * 100).toInt()

        // Set data to views
        holder.activityId.text = "Activity ID: ${currentitem.activityid}"
        holder.name.text = "${currentitem.name} \n"
        holder.status.text = "Status: ${currentitem.status}"
        holder.date.text = " ${currentitem.date}"
        holder.donationReceived.text = "RM ${currentitem.totalDonationReceived}"
        holder.totalRequired.text = "RM ${currentitem.totalRequired}"
        holder.userId.text = "User ID: ${currentitem.userId}"
        holder.progressBar.progress = progress

        holder.editButton.setOnClickListener {
//  Log.d(TAG, "Edit button clicked for activity ID: ${currentitem.activityId}")

            // Create an Intent to navigate to the target activity (AdminActivityUpdateActivity)
            val intent = Intent(context, AdminActivityRetrieveActivity::class.java)


            intent.putExtra("activityId", currentitem.activityid)

            context.startActivity(intent)

        }
    }

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityId: TextView = itemView.findViewById(R.id.activityIdTextView)
        val name: TextView = itemView.findViewById(R.id.nameTextView)
        val status: TextView = itemView.findViewById(R.id.statusTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val donationReceived: TextView = itemView.findViewById(R.id.donationReceivedTextView)
        val totalRequired: TextView = itemView.findViewById(R.id.totalRequiredTextView)
        val userId: TextView = itemView.findViewById(R.id.userIdTextView)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val editButton: Button = itemView.findViewById(R.id.editActivityButton)
    }

}