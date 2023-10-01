package com.example.assignment.AdminFragment

import android.content.ContentValues
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Adapter.ActivityAdapter2
import com.example.assignment.Adapter.ActivityAdapter3
import com.example.assignment.Model.Activity
import com.example.assignment.Model.User2
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text


class AdminHomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivityAdapter3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.admin_fragment_home, container, false)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Use the NavController to navigate to the specified action
                findNavController().navigate(R.id.action_adminHomeFragment_to_adminLoginActivity)

            }
        }

        // Add the callback to the fragment's lifecycle
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        val db = FirebaseFirestore.getInstance()
        val activityCollection = db.collection("activity")
        val userCollection = db.collection("user")

        recyclerView               = view.findViewById(R.id.adminHomeActivityRecycler)
        recyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        adapter                    = ActivityAdapter3(requireContext(),requireFragmentManager(), mutableListOf())
        recyclerView.adapter       = adapter

        val activityControltext = view.findViewById<TextView>(R.id.adminActivityControlView)

        /// Fetch activity data from Firestore
        activityCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val activityList = mutableListOf<Activity>()
                for (document in querySnapshot) {

                    val status = document.getString("status") ?: ""
                    if(status == "pending") {
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

                adapter.activityList = activityList
                adapter.notifyDataSetChanged()

                if (activityList.isEmpty()) {
                    activityControltext.visibility = View.VISIBLE
                } else {
                    activityControltext.visibility = View.GONE
                }

            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error fetching Firestore data: $exception")
            }



        return view
    }


}