package org.example.model

import java.time.Duration
import java.time.LocalDateTime

data class Attendance(
    val employeeId: String,
    var dateTimeOfCheckIn: LocalDateTime,
    var dateTimeOfCheckOut: LocalDateTime? = null,
    var workedHours: String? = null // now stored instead of computed
) {
    fun checkOut(dateTime: LocalDateTime) {
        this.dateTimeOfCheckOut = dateTime

        // Calculate worked hours immediately and store it
        val duration = Duration.between(dateTimeOfCheckIn, dateTime)
        this.workedHours = "${duration.toHours()}h ${duration.toMinutesPart()}m"
    }
}