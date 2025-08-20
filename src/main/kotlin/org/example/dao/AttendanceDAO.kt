package org.example.dao

import org.jdbi.v3.core.Jdbi
import org.example.model.Attendance
import java.time.LocalDate

class AttendanceDAO(private val jdbi: Jdbi) {

    fun insert(att: Attendance): Int {
        return jdbi.withHandle<Int, Exception> { handle ->
            handle.createUpdate(
                """
                INSERT INTO attendance (employee_id, check_in_time, check_out_time, worked_hours)
                VALUES (:employeeId, :dateTimeOfCheckIn, :dateTimeOfCheckOut, :workedHours)
                """
            )
                .bindBean(att)
                .execute()
        }
    }

    fun update(att: Attendance): Int {
        return jdbi.withHandle<Int, Exception> { handle ->
            handle.createUpdate("""
            UPDATE attendance
            SET check_out_time = :dateTimeOfCheckOut,
                worked_hours = :workedHours
            WHERE employee_id = :employeeId AND check_in_time = :dateTimeOfCheckIn
        """).bindBean(att)
                .execute()
        }
    }


    fun findByEmployeeAndDate(employeeId: String, date: LocalDate): List<Attendance> {
        val start = date.atStartOfDay()
        val end = date.plusDays(1).atStartOfDay()
        return jdbi.withHandle<List<Attendance>, Exception> { handle ->
            handle.createQuery(
                """
            SELECT employee_id AS "employeeId",
                   check_in_time AS "dateTimeOfCheckIn",
                   check_out_time AS "dateTimeOfCheckOut",
                   worked_hours AS "workedHours"
            FROM attendance
            WHERE employee_id = :employeeId
              AND check_in_time >= :start AND check_in_time < :end
            """
            )
                .bind("employeeId", employeeId)
                .bind("start", start)
                .bind("end", end)
                .mapTo(Attendance::class.java)
                .list()
        }
    }


    fun findOpenAttendanceForToday(employeeId: String, date: LocalDate): Attendance? {
        return jdbi.withHandle<Attendance?, Exception> { handle ->
            handle.createQuery(
                """
            SELECT employee_id AS "employeeId",
                   check_in_time AS "dateTimeOfCheckIn",
                   check_out_time AS "dateTimeOfCheckOut",
                   worked_hours AS "workedHours"
            FROM attendance
            WHERE employee_id = :employeeId
              AND DATE(check_in_time) = :date
              AND check_out_time IS NULL
            ORDER BY check_in_time DESC
            LIMIT 1
            """
            )
                .bind("employeeId", employeeId)
                .bind("date", date)
                .mapTo(Attendance::class.java)
                .findOne()
                .orElse(null)
        }
    }


}
