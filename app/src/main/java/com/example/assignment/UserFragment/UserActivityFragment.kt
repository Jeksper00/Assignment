package com.example.assignment.UserFragment

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Activity
import com.example.assignment.AdminActivityRetrieveActivity
import com.example.assignment.R
import com.example.assignment.UserActivityAdapter
import com.example.assignment.UserActivityCreateActivity
import com.example.assignment.UserLoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class UserActivityFragment : Fragment() {
    private lateinit var activityRecyclerView: RecyclerView
    private lateinit var adapter: UserActivityAdapter
    private var allActivities: MutableList<Activity> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_fragment_activity, container, false)

        //Back button on screen
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Use the NavController to navigate to the specified action
                val intent = Intent(requireActivity(), UserLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                requireActivity().startActivity(intent)
            }
        }

        // Add the callback to the fragment's lifecycle
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)


        // Button to create a new activity
        view.findViewById<Button>(R.id.createActivity).setOnClickListener {
            startActivity(Intent(requireActivity(), UserActivityCreateActivity::class.java))
            //requireActivity().finish()
        }

        // RecyclerView for activities
        activityRecyclerView = view.findViewById(R.id.activityList)
        activityRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        adapter = UserActivityAdapter(requireContext(), requireFragmentManager(), mutableListOf())
        activityRecyclerView.adapter = adapter

        // Search functionality for filtering activities
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

        // Fetch user data and activities
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid
        if (userUid != null) {
            val firestore = FirebaseFirestore.getInstance()
            val userCollection = firestore.collection("user")

            userCollection.whereEqualTo("id", userUid)
                .get()
                .addOnSuccessListener { userQuerySnapshot ->
                    if (!userQuerySnapshot.isEmpty) {
                        val userDocument = userQuerySnapshot.documents[0]
                        val userIdUser = userDocument.id

                        fetchActivityData(userIdUser)
                    } else {
                        Toast.makeText(requireContext(), "No user found with this UID.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error fetching user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    // Function to generate a unique document ID for an activity
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

    // Function to filter activities based on query
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

    // Function to fetch user data based on UID
    private fun fetchUserData(userUid: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userCollection = firestore.collection("user")

        userCollection.whereEqualTo("id", userUid)
            .get()
            .addOnSuccessListener { userQuerySnapshot ->
                if (!userQuerySnapshot.isEmpty) {
                    val userDocument = userQuerySnapshot.documents[0]
                    val userIdUser = userDocument.id

                    fetchActivityData(userIdUser)
                } else {
                    Toast.makeText(requireContext(), "No user found with this UID.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching user data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to fetch activity data based on userID
    private fun fetchActivityData(userIdUser: String) {
        val firestore = FirebaseFirestore.getInstance()
        val activityCollection = firestore.collection("activity")

        activityCollection.whereEqualTo("userid", userIdUser)
            .get()
            .addOnSuccessListener { activityQuerySnapshot ->
                val activityList = mutableListOf<Activity>()

                for (activityDocument in activityQuerySnapshot) {
                    val status = activityDocument.getString("status") ?: ""
                    val userId = activityDocument.getString("userid") ?: ""

                    if (status != "admin" && userId == userIdUser) {
                        val id = activityDocument.reference.id
                        val name = activityDocument.getString("name") ?: ""
                        val imageUrl = activityDocument.getString("imageUrl") ?: ""
                        val description = activityDocument.getString("description") ?: ""
                        val date = activityDocument.getString("date") ?: ""
                        val donationReceivedString = activityDocument.getString("totalDonationReceived") ?: ""
                        val donationReceived = donationReceivedString?.toDoubleOrNull() ?: 0.0
                        val totalRequiredString = activityDocument.getString("totalRequired") ?: ""
                        val totalRequired = totalRequiredString?.toDoubleOrNull() ?: 0.0

                        val activityItem = Activity(
                            id,
                            name,
                            status,
                            description,
                            date,
                            donationReceived,
                            totalRequired,
                            userId,
                            imageUrl
                        )
                        activityList.add(activityItem)
                    }
                }

                // Update UI with the filtered activity list
                allActivities.clear()
                allActivities.addAll(activityList)
                adapter.activityList = activityList
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching activity data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}