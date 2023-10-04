package com.example.assignment.UserFragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Adapter.ActivityAdapter2
import com.example.assignment.Model.Activity
import com.example.assignment.Model.User2
import com.example.assignment.R
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore


class UserHomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivityAdapter2
    private lateinit var tabLayout: TabLayout

    // Declare a list to hold all activities
    private var allActivities: MutableList<Activity> = mutableListOf()
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
        val userCollection = db.collection("user")

        recyclerView               = view.findViewById(R.id.adminHomeActivityRecycler)
        recyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        adapter                    = ActivityAdapter2(requireContext(),requireFragmentManager(), mutableListOf())
        recyclerView.adapter       = adapter




        //Back button on screen
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Use the NavController to navigate to the specified action
                findNavController().navigate(R.id.action_userHomeFragment_to_userLoginActivity)
                requireActivity().finish()
            }
        }

        // Add the callback to the fragment's lifecycle
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        //TabView
        tabLayout = view.findViewById(R.id.user_home_activityList_category_tab)
        val tabTitles = listOf("Third-Party", "Official")

        // Inside onViewCreated or onCreateView, add the following code to set up search functionality
        val searchView = view.findViewById<SearchView>(R.id.user_activityList_searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission if needed
                if (query.isNullOrEmpty()) {
                    // If the text is empty or null, clear the query and show all activities
                    adapter.activityList = allActivities
                    adapter.notifyDataSetChanged()
                    searchView.clearFocus()
                }
                // Close the keyboard
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the activity list based on the search query

                // Filter the activity list based on the search query
                val filteredActivities = filterActivities(newText)
                adapter.activityList = filteredActivities
                adapter.notifyDataSetChanged()

                return true
            }
        })

        // Set up a click listener for the main layout to close the keyboard when clicked outside
        val mainLayout = view.findViewById<View>(R.id.user_home_activity_fragment)
        mainLayout.setOnClickListener {
            searchView.clearFocus()
        }

        recyclerView.setOnClickListener {
//            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(searchView.windowToken, 0)
            searchView.clearFocus()
        }

        // Get a reference to the root layout (assuming it's a ConstraintLayout)
        val rootLayout = view.findViewById<ConstraintLayout>(R.id.user_home_activity_fragment2)

        /// Fetch activity data from Firestore
        activityCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val activityList = mutableListOf<Activity>()
                for (document in querySnapshot) {
                    val status = document.getString("status") ?: ""
                    if(status == "approve") {
                        val id                    = document.reference.id
                        val imageUrl              = document.getString("imageUrl") ?: ""
                        val name                  = document.getString("name") ?: ""
                        val description           = document.getString("description") ?: ""
                        val date                  = document.getString("date") ?: ""
                        val totalDonationReceived = document.getString("totalDonationReceived") ?: ""
                        val totalRequired         = document.getString("totalRequired") ?: ""
                        val userId                = document.getString("userid") ?: ""
                        val user = User2()
                        val userCollection = db.collection("user")
                        userCollection.document(userId).get().addOnSuccessListener{ userDocument ->
                                val userName = userDocument.getString("name")?:""
                                val user = User2(userName)
                        }
                        val activityItem      = Activity(id, imageUrl, name, status, description
                            , date, totalDonationReceived, totalRequired,  userId)
                        activityList.add(activityItem)

                    }
                }

                // Update the allActivities list
                allActivities.clear()
                allActivities.addAll(activityList)

                adapter.activityList = activityList
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error fetching Firestore data: $exception")
            }


//        val receivedData = arguments?.getString("activityCategory") // Replace "key" with the key you used
//        if (receivedData == "admin") {
//            val bundle = Bundle()
////            bundle.putString("activityCategory", "") // Put the data you want to pass here
//            //Set the default tab position (e.g., select the second tab)
//            val defaultTabPosition = 1 // Index of the tab you want to select
//            val tab = tabLayout.getTabAt(defaultTabPosition)
//            tab?.select()
//        }else{
//
//        }


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Check which tab is selected and update the text accordingly
                when (tab?.position) {
                    //For Third-Party Tab
                    0 -> // Fetch activity data from Firestore
                        activityCollection.get()
                            .addOnSuccessListener { querySnapshot ->
                                val activityList = mutableListOf<Activity>()
                                for (document in querySnapshot) {
                                    val status = document.getString("status") ?: ""
                                    if(status == "approve") {
                                        val id                    = document.reference.id
                                        val imageUrl              = document.getString("imageUrl") ?: ""
                                        val name                  = document.getString("name") ?: ""
                                        val description           = document.getString("description") ?: ""
                                        val date                  = document.getString("date") ?: ""
                                        val totalDonationReceived = document.getString("totalDonationReceived") ?: ""
                                        val totalRequired         = document.getString("totalRequired") ?: ""
                                        val userId                = document.getString("userid") ?: ""
                                        val activityItem      = Activity(id, imageUrl, name, status, description
                                            , date, totalDonationReceived, totalRequired,  userId)
                                        activityList.add(activityItem)

                                    }
                                }

                                // Update the allActivities list
                                allActivities.clear()
                                allActivities.addAll(activityList)

                                adapter.activityList = activityList
                                adapter.notifyDataSetChanged()

                            }
                            .addOnFailureListener { exception ->
                                Log.e(ContentValues.TAG, "Error fetching Firestore data: $exception")
                            }
                    //For Official Tab
                    1 -> // Fetch activity data from Firestore
                        activityCollection.get()
                            .addOnSuccessListener { querySnapshot ->
                                val activityList = mutableListOf<Activity>()
                                for (document in querySnapshot) {
                                    val status = document.getString("status") ?: ""
                                    if(status == "admin") {
                                        val id                    = document.reference.id
                                        val imageUrl              = document.getString("imageUrl") ?: ""
                                        val name                  = document.getString("name") ?: ""
                                        val description           = document.getString("description") ?: ""
                                        val date                  = document.getString("date") ?: ""
                                        val totalDonationReceived = document.getString("totalDonationReceived") ?: ""
                                        val totalRequired         = document.getString("totalRequired") ?: ""
                                        val userId                = "Official"
                                        val activityItem      = Activity(id, imageUrl, name, status, description
                                            , date, totalDonationReceived, totalRequired,  userId)
                                        activityList.add(activityItem)

                                    }
                                }



                                // Update the allActivities list
                                allActivities.clear()
                                allActivities.addAll(activityList)

                                adapter.activityList = activityList
                                adapter.notifyDataSetChanged()

                            }
                            .addOnFailureListener { exception ->
                                Log.e(ContentValues.TAG, "Error fetching Firestore data: $exception")
                            }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing here
            }
        })



        return view
    }

    private fun filterActivities(query: String?): MutableList<Activity> {
        val filteredList = mutableListOf<Activity>()
        query?.let { searchText ->
            for (activity in allActivities) {
                val nameMatch = activity.name.toLowerCase().contains(searchText.toLowerCase())
                val userMatch = activity.userId.contains(searchText.toLowerCase())
                val dateMatch = activity.date.toLowerCase().contains(searchText.toLowerCase())

                if (nameMatch || userMatch || dateMatch) {
                    filteredList.add(activity)
                }
            }
        }
        return filteredList
    }

}