
    package org.example.dto

    import java.time.LocalDateTime

    data class CheckOutRequest(
        val employeeId: String,
        val dateTime: String? = null
    )

