package com.example.assignment.Model

data class Activity(
    val name: String,
    val status: String,
    val description: String,
    val date: String,
    val totalDonationReceived: String,
    val totalRequired: Double,
    val userId: String
)
