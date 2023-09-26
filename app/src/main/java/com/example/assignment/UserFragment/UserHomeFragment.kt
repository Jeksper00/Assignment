package com.example.assignment.UserFragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.Adapter.ActivityAdapter2
import com.example.assignment.Adapter.NotificationAdapter
import com.example.assignment.AdminFragment.AdminNotificationCreateFragment
import com.example.assignment.Model.Activity
import com.example.assignment.Model.Notification
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore


class UserHomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivityAdapter2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.user_fragment_home, container, false)
        val view2 = inflater.inflate(R.layout.layout_activity_list_user, container, false)

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()
        val activityCollection = db.collection("activity")


        recyclerView = view.findViewById(R.id.userHomeActivityRecycler)
        recyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        adapter = ActivityAdapter2(requireContext(),requireFragmentManager(), mutableListOf())
        recyclerView.adapter = adapter

        // Fetch notification data from Firestore
        activityCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val activityList = mutableListOf<Activity>()
                for (document in querySnapshot) {
                    val status = document.getString("status") ?: ""
                    if(status == "Approve") {
                        val id = document.reference.id
                        val imageUrl = document.getString("imageUrl") ?: ""
                        val name = document.getString("name") ?: ""
                        val description = document.getString("description") ?: ""
                        val date = document.getString("date") ?: ""
                        val totalDonationReceived = document.getString("totalDonationReceived") ?: ""
                        val totalRequired = document.getString("totalRequired") ?: ""
                        val userId = document.getString("userid") ?: ""
                        val notificationItem = Activity(id, imageUrl, name, status, description
                            , date, totalDonationReceived, totalRequired,  userId)
                        activityList.add(notificationItem)

                    }
                }

                adapter.activityList = activityList
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error fetching Firestore data: $exception")
            }

        return view
    }

}