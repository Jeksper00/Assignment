package com.example.assignment.AdminFragment

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Activity
import com.example.assignment.ActivityAdapter
import com.example.assignment.AdminActivityCreateActivity
import com.example.assignment.R
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivityFragment : Fragment(){
    private lateinit var activityRecyclerView: RecyclerView
    private lateinit var adapter: ActivityAdapter
    private var allActivities: MutableList<Activity> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.admin_fragment_activity, container, false)

        var num = 0


        val db = FirebaseFirestore.getInstance()
        val activityCollection = db.collection("activity")
        val intent = Intent(requireActivity(), AdminActivityCreateActivity::class.java)
        generateDocumentId(num, activityCollection) { documentId ->

            intent.putExtra("activityId", documentId)

        }

        view.findViewById<Button>(R.id.createActivity).setOnClickListener {
            startActivity(intent)
            requireActivity().finish()
        }

        activityRecyclerView = view.findViewById(R.id.activityList)
        activityRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        adapter = ActivityAdapter(requireContext(), requireFragmentManager(), mutableListOf())
        activityRecyclerView.adapter = adapter

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredActivities = filterActivities(newText)
                adapter.activityList = filteredActivities
                adapter.notifyDataSetChanged()
                return true
            }
        })

        activityCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val activityList = mutableListOf<Activity>()
                for (document in querySnapshot) {
                    val status = document.getString("status") ?: ""
                    if (status == "admin") {
                        val id = document.reference.id
                        val name = document.getString("name") ?: ""
                        val imageUrl = document.getString("imageUrl") ?: ""
                        val description = document.getString("description") ?: ""
                        val date = document.getString("date") ?: ""
                        val donationReceivedString = document.getString("totalDonationReceived") ?: ""
                        val donationReceived = donationReceivedString?.toDoubleOrNull() ?: 0.0
                        val totalRequiredString = document.getString("totalRequired") ?: ""
                        val totalRequired = totalRequiredString?.toDoubleOrNull() ?: 0.0
                        val userId = document.getString("userid") ?: ""

                        val activityItem = Activity(
                            id, name, status, description, date, donationReceived, totalRequired, userId, imageUrl
                        )
                        activityList.add(activityItem)
                    }
                }

                allActivities.clear()
                allActivities.addAll(activityList)

                adapter.activityList = activityList
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error fetching Firestore data: $exception")
            }

        return view
    }


    private fun generateDocumentId(num: Int, collectionRef: CollectionReference, callback: (String) -> Unit) {
        val formattedCounter = String.format("%04d", num)
        val documentIdToCheck = "A$formattedCounter"

        collectionRef.document(documentIdToCheck)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    generateDocumentId(num + 1, collectionRef, callback)
                } else {
                    callback(documentIdToCheck)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error getting document: $exception")
            }
    }

    private fun filterActivities(query: String?): MutableList<Activity> {
        val filteredList = mutableListOf<Activity>()
        query?.let { searchText ->
            for (activity in allActivities) {
                val idMatch = activity.activityid.toLowerCase().contains(searchText.toLowerCase())
                val nameMatch = activity.name.toLowerCase().contains(searchText.toLowerCase())
                val userMatch = activity.userId.toLowerCase().contains(searchText.toLowerCase())
                val dateMatch = activity.date.toLowerCase().contains(searchText.toLowerCase())

                if (nameMatch || userMatch || dateMatch || idMatch) {
                    filteredList.add(activity)
                }
            }
        }
        return filteredList
    }
}