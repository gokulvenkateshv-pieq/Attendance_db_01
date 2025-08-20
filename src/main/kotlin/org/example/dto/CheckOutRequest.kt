package org.example.dto

data class CheckOutRequest(
        val employeeId: String,
        val dateTime: String? = null
    )

