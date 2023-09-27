package com.example.assignment.AdminFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.assignment.R

import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Adapter.FeedbackAdapter
import com.example.assignment.Model.Feedback
import com.google.firebase.firestore.FirebaseFirestore


class AdminFeedbackViewFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FeedbackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.admin_fragment_feedback_view, container, false)

        view.findViewById<ImageView>(R.id.admin_feedbackView_backButton).setOnClickListener{
            val notificationFragment = AdminFeedbackFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.admin_fl_wrapper, notificationFragment)
            transaction.addToBackStack(null) // Add to back stack
            transaction.commit()
        }

        recyclerView = view.findViewById(R.id.adminFeedbackRecycle)
        recyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        adapter = FeedbackAdapter(requireContext(),requireFragmentManager(), mutableListOf())
        recyclerView.adapter = adapter

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()
        val feedbackCollection = db.collection("feedback")

        // Fetch feedback data from Firestore
        feedbackCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val feedbackList = mutableListOf<Feedback>()
                for (document in querySnapshot) {
                    val id = document.reference.id
                    val gmail = document.getString("gmail") ?: ""
                    val feedback = document.getString("feedback") ?: ""
                    val feedbackItem = Feedback(id, gmail, feedback)
                    feedbackList.add(feedbackItem)
                }

                adapter.feedbackList = feedbackList
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }

        return view
    }


}