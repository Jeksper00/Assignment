package com.example.assignment.UserFragment

import android.content.ContentValues
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Adapter.NotificationAdapter
import com.example.assignment.Adapter.NotificationAdapter2
import com.example.assignment.Model.Notification
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore


class UserNotificationFragment : Fragment() {

//    lateinit var fragmentManager : FragmentManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter2

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): android.view.View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.user_fragment_notification, container, false)

        //Back button on screen
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Use the NavController to navigate to the specified action
                findNavController().navigate(R.id.action_userNotificationFragment_to_userLoginActivity)

            }
        }
        // Add the callback to the fragment's lifecycle
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)


        view.findViewById<Button>(R.id.userFeedbackButton).setOnClickListener{
            openFragment(UserFeedbackInputFragment())
        }

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()
        val notificationCollection = db.collection("notification")


        recyclerView               = view.findViewById(R.id.userNotificationRecycler)
        recyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        adapter                    = NotificationAdapter2(requireContext(), mutableListOf())
        recyclerView.adapter       = adapter

        // Fetch notification data from Firestore
        notificationCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val notificationList = mutableListOf<Notification>()
                for (document in querySnapshot) {
                    val id               = document.reference.id
                    val title            = document.getString("title") ?: ""
                    val description      = document.getString("description") ?: ""
                    val date             = document.getString("date") ?: ""
                    val notificationItem = Notification(id, title, description, date)
                    notificationList.add(notificationItem)
                }

                adapter.notificationList = notificationList
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error fetching Firestore data: $exception")
            }

        return view
    }

    private fun openFragment(fragment : Fragment){
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.user_fl_wrapper, fragment)
        fragmentTransaction.addToBackStack(null) // Optional, to allow back navigation
        fragmentTransaction.commit()
    }
}

