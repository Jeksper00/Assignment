package com.example.assignment.AdminFragment

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Adapter.UserListAdapter
import com.example.assignment.Model.User
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore


class AdminManageUserFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserListAdapter

    // Declare a list to hold all activities
    private var allUsers: MutableList<User> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.admin_fragment_manageuser, container, false)

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("user")

        recyclerView = view.findViewById(R.id.userListRecycler)
        recyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        adapter = UserListAdapter(requireContext(),requireFragmentManager(), mutableListOf())
        recyclerView.adapter = adapter

        // Search function
        val searchView = view.findViewById<SearchView>(R.id.admin_searchuser_bar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission if needed
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchView.windowToken, 0)

                if (query.isNullOrEmpty()) {
                    // If the text is empty or null, clear the query and show all users
                    adapter.userList = allUsers
                    adapter.notifyDataSetChanged()
                }
                // Close the keyboard
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the user list based on the search query
                val filteredUsers = filterUsers(newText)
                adapter.userList = filteredUsers
                adapter.notifyDataSetChanged()

                return true
            }
        })

        // Set up a click listener for the main layout to close the keyboard when clicked outside
        val mainLayout = view.findViewById<View>(R.id.admin_manageuser_frame_fragment)
        mainLayout.setOnClickListener {
            searchView.clearFocus()
        }

        // Get a reference to the root layout (assuming it's a ConstraintLayout)
        val rootLayout = view.findViewById<ConstraintLayout>(R.id.admin_manageuser_constraint_fragment)

        // Fetch notification data from Firestore
        userCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val userList = mutableListOf<User>()
                for (document in querySnapshot) {
                    val id               = document.reference.id
                    val profile_img      = document.getString("personal_img") ?: ""
                    val name             = document.getString("name") ?: ""
                    val email            = document.getString("email") ?: ""
                    val contact_no       = document.getString("contact") ?: ""
                    val gender           = document.getString("gender") ?: ""
                    val password         = document.getString("password") ?: ""
                    val user = User(id, profile_img, name, email, contact_no, gender, password)
                    userList.add(user)
                }

                // Update the allActivities list
                allUsers.clear()
                allUsers.addAll(userList)

                adapter.userList = userList
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error fetching Firestore data: $exception")
            }

        return view
    }

    // Filter users within search function
    private fun filterUsers(query: String?): MutableList<User> {
        val filteredList = mutableListOf<User>()
        query?.let { searchText ->
            for (user in allUsers) {
                val nameMatch = user.name.toLowerCase().contains(searchText.toLowerCase())
                val emailMatch = user.email.toLowerCase().contains(searchText.toLowerCase())

                if (nameMatch || emailMatch) {
                    filteredList.add(user)
                }
            }
        }
        return filteredList
    }
}