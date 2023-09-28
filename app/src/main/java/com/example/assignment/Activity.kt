package com.example.assignment

data class Activity(
    val activityid: String,
    val name: String,
    val status: String,
    val description: String,
    val date: String,
    val totalDonationReceived: Double, // Assuming it's a Double, adjust the type as needed
    val totalRequired: Double, // Assuming it's a Double, adjust the type as needed
    val userId: String,
    val imageUrl: String
)