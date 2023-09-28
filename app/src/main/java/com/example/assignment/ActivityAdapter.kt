package com.example.assignment





import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

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








         //Set click listeners for Retrieve
        holder.editButton.setOnClickListener {
//  Log.d(TAG, "Edit button clicked for activity ID: ${currentitem.activityId}")

            // Create an Intent to navigate to the target activity (AdminActivityUpdateActivity)
            val intent = Intent(context, AdminActivityRetrieveActivity::class.java)



            intent.putExtra("activityId", currentitem.activityid)


            context.startActivity(intent)

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