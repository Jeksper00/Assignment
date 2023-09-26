package com.example.assignment.AdminFragment

import android.content.ContentValues.TAG
import com.example.assignment.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.ActivityAdapter
import com.example.assignment.AdminActivityCreateActivity
import com.example.assignment.R
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class AdminActivityFragment : Fragment() {


    private lateinit var activityRecyclerView: RecyclerView
    private lateinit var adapter: ActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.admin_fragment_activity, container, false)

        val db = FirebaseFirestore.getInstance()
        val activityCollection = db.collection("activity")


        view.findViewById<Button>(R.id.createActivity).setOnClickListener {
            val intent = Intent(requireActivity(), AdminActivityCreateActivity::class.java)
            var num = 1

            val db = FirebaseFirestore.getInstance()
            val collectionRef = db.collection("activity")

            generateDocumentId(num, collectionRef) { documentId ->
                // Document ID generation is complete
                intent.putExtra("activityId", documentId)
                startActivity(intent)
            }
        }





        activityRecyclerView = view.findViewById(R.id.activityList)
        activityRecyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        adapter = ActivityAdapter(requireContext(),requireFragmentManager(), mutableListOf())
        activityRecyclerView.adapter = adapter

        // Fetch notification data from Firestore
        activityCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val activityList = mutableListOf<Activity>()
                for (document in querySnapshot) {
                    val id = document.reference.id
                    val name = document.getString("name") ?: ""
                    val status = document.getString("status") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val description = document.getString("description") ?: ""
                    val date = document.getString("date") ?: ""
                    val donationReceivedString = document.getString("donationReceived") ?: ""
                    val donationReceived = donationReceivedString?.toDoubleOrNull() ?: 0.0
                    val totalRequiredString = document.getString("totalRequired") ?: ""
                    val totalRequired = totalRequiredString?.toDoubleOrNull()?: 0.0
                    val userId = document.getString("userId") ?: ""
                    // Check if userId is empty /////////////////////////and also check validation

                        val activityItem = Activity(id, name, status, description, date, donationReceived, totalRequired, userId,imageUrl)
                        activityList.add(activityItem)

                }

                adapter.activityList = activityList
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching Firestore data: $exception")
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
                    // Document with this ID exists, try the next one
                    generateDocumentId(num + 1, collectionRef, callback)
                } else {
                    // Document with this ID doesn't exist, callback with the ID
                    callback(documentIdToCheck)
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred while querying Firestore
                Log.e(TAG, "Error getting document: $exception")
            }
    }


}
