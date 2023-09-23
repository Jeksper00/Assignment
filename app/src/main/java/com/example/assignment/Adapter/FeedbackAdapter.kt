package com.example.assignment.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Model.Feedback
import com.example.assignment.R

class FeedbackAdapter(public var feedbackList: List<Feedback>) :
    RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gmailTextView: TextView = itemView.findViewById(R.id.feedbackGmail)
        val feedbackTextView: TextView = itemView.findViewById(R.id.feedbackText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_feedback_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.gmailTextView.text = feedback.gmail
        holder.feedbackTextView.text = feedback.feedback
    }

    override fun getItemCount(): Int {
        return feedbackList.size
    }
}