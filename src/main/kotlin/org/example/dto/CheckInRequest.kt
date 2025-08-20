package org.example.dto

data class CheckInRequest(
    val employeeId: String,
    val dateTime: String? // optional, can be null
)
