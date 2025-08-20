package org.example.service

import org.example.model.Employee
import org.example.dao.EmployeeDAO
import org.example.model.Role
import org.example.model.Department
import org.example.model.Attendance
import org.example.dao.AttendanceDAO
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime

class EmployeeService(
    private val employeeDao: EmployeeDAO,
    private val attendanceDao: AttendanceDAO
) {
    private val log = LoggerFactory.getLogger(EmployeeService::class.java)

    // -------------------- Employee CRUD --------------------

    fun addEmployee(
        firstName: String,
        lastName: String,
        role: Role,
        department: Department,
        reportingTo: String?
    ): Pair<Response.Status, Any> {
        val emp = Employee(
            firstName = firstName,
            lastName = lastName,
            role = role.name,
            department = department.name,
            reportingTo = reportingTo
        )

        return try {
            emp.generateId() // ensure employeeId is generated
            employeeDao.insert(emp, role.ordinal + 1, department.ordinal + 1)
            log.info("Employee added successfully id=${emp.employeeId}")
            Response.Status.CREATED to emp
        } catch (e: Exception) {
            log.error("Error inserting employee", e)
            Response.Status.CONFLICT to mapOf("error" to (e.message ?: "Unknown error"))
        }
    }

    fun getEmployee(id: String): Pair<Response.Status, Any> {
        val emp = employeeDao.getById(id)
        return if (emp != null) {
            Response.Status.OK to emp
        } else {
            Response.Status.NOT_FOUND to mapOf("error" to "Employee not found")
        }
    }

    fun getAllEmployees(limit: Int = 20): List<Employee> = employeeDao.getAll(limit)

    fun deleteEmployee(id: String): Pair<Response.Status, Any> {
        val deleted = employeeDao.delete(id)
        return if (deleted > 0) {
            Response.Status.OK to mapOf("message" to "Employee deleted successfully")
        } else {
            Response.Status.NOT_FOUND to mapOf("error" to "Employee not found")
        }
    }


    // -------------------- Attendance --------------------

    fun checkIn(employeeId: String, dateTime: LocalDateTime? = null): Pair<Response.Status, Any> {
        return try {
            //  Validate employee exists
            val emp = employeeDao.getById(employeeId)
            if (emp == null) {
                return Response.Status.NOT_FOUND to mapOf("error" to "Employee ID $employeeId does not exist")
            }

            //  Determine check-in time (rounded to minute)
            val now = (dateTime ?: LocalDateTime.now()).withSecond(0).withNano(0)

            //  Check for duplicate timestamp at the same minute
            val sameTimeAttendance = attendanceDao.findByEmployeeAndDate(employeeId, now.toLocalDate())
                .any { it.dateTimeOfCheckIn.withSecond(0).withNano(0) == now }

            if (sameTimeAttendance) {
                return Response.Status.CONFLICT to mapOf("error" to "Cannot check in multiple times at the same minute")
            }

            //  Insert attendance
            val attendance = Attendance(employeeId, now)
            attendanceDao.insert(attendance)
            Response.Status.CREATED to attendance
        } catch (e: Exception) {
            log.error("Error during check-in", e)
            Response.Status.CONFLICT to mapOf("error" to (e.message ?: "Unknown error"))
        }
    }

    fun checkOut(employeeId: String, dateTime: LocalDateTime? = null): Pair<Response.Status, Any> {
        val today = LocalDate.now()
        val openAttendance = attendanceDao.findOpenAttendanceForToday(employeeId, today)
            ?: return Response.Status.NOT_FOUND to mapOf("error" to "No active check-in found for today")

        val now = (dateTime ?: LocalDateTime.now()).withSecond(0).withNano(0)
        openAttendance.checkOut(now)
        attendanceDao.update(openAttendance)
        return Response.Status.OK to openAttendance
    }


    fun getAttendance(employeeId: String?, date: LocalDate?): List<Attendance> {
        return if (employeeId != null && date != null) {
            attendanceDao.findByEmployeeAndDate(employeeId, date)
        } else {
            emptyList()
        }
    }
}

