package com.example.assignment.Adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Model.Notification
import com.example.assignment.R



class NotificationAdapter2 (private val context: Context, public var notificationList: MutableList<Notification>) :
    RecyclerView.Adapter<NotificationAdapter2.ViewHolder>() {


    companion object {
        const val ARG_NOTIFICATION = "notification"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView = itemView.findViewById(R.id.notificationIdView)
        val titleTextView: TextView = itemView.findViewById(R.id.notificationTitleView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.notificationDescriptionView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_notification_list_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.idTextView.text = notification.id
        holder.titleTextView.text = notification.title
        holder.descriptionTextView.text = notification.description
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}