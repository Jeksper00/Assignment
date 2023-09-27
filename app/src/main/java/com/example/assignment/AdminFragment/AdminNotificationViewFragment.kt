package com.example.assignment.AdminFragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Adapter.NotificationAdapter
import com.example.assignment.Model.Notification
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore

class AdminNotificationViewFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val notificationFragment = AdminFeedbackFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.admin_fl_wrapper, notificationFragment)
            transaction.addToBackStack(null) // Add to back stack
            transaction.commit()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.admin_fragment_notification_view, container, false)

        view.findViewById<ImageView>(R.id.admin_notification_view_backButton).setOnClickListener{
            val notificationFragment = AdminFeedbackFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.admin_fl_wrapper, notificationFragment)
            transaction.addToBackStack(null) // Add to back stack
            transaction.commit()
        }
        // Register the callback
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)


        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()
        val notificationCollection = db.collection("notification")

//        view.findViewById<Button>(R.id.adminAddNotificationButton).setOnClickListener{
//            openFragment(AdminNotificationCreateFragment())
//        }
        view.findViewById<ImageView>(R.id.admin_add_button_image).setOnClickListener{
            openFragment(AdminNotificationCreateFragment())
        }


        recyclerView = view.findViewById(R.id.adminNotificationRecycler)
        recyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        adapter = NotificationAdapter(requireContext(),requireFragmentManager(), mutableListOf())
        recyclerView.adapter = adapter

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
                Log.e(TAG, "Error fetching Firestore data: $exception")
            }

        return view
    }

    private fun openFragment(fragment : Fragment){
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.admin_fl_wrapper, fragment)
        fragmentTransaction.addToBackStack(null) // Optional, to allow back navigation
        fragmentTransaction.commit()
    }

}