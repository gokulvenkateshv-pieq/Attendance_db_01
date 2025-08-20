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

    fun login(employeeId: String, password: String): Pair<Response.Status, Any> {
        val emp = employeeDao.getById(employeeId)
        return if (emp == null) {
            Response.Status.NOT_FOUND to mapOf("error" to "Employee not found")
        } else if (password != "password123") { // replace with real auth logic
            Response.Status.UNAUTHORIZED to mapOf("error" to "Invalid credentials")
        } else {
            Response.Status.OK to emp
        }
    }

    // -------------------- Attendance --------------------

    fun checkIn(employeeId: String, dateTime: LocalDateTime? = null): Pair<Response.Status, Any> {
        return try {
            val now = dateTime ?: LocalDateTime.now()
            val attendance = Attendance(employeeId, now)
            attendanceDao.insert(attendance)
            Response.Status.CREATED to attendance
        } catch (e: Exception) {
            log.error("Error during check-in", e)
            Response.Status.CONFLICT to mapOf("error" to (e.message ?: "Unknown error"))
        }
    }

    fun checkOut(employeeId: String, dateTime: LocalDateTime? = null): Pair<Response.Status, Any> {
        return try {
            val openAttendance = attendanceDao.findOpenAttendance(employeeId)
            if (openAttendance == null) {
                Response.Status.NOT_FOUND to mapOf("error" to "No active check-in found")
            } else {
                openAttendance.checkOut(dateTime ?: LocalDateTime.now())
                attendanceDao.insert(openAttendance) // or use update method if implemented
                Response.Status.OK to openAttendance
            }
        } catch (e: Exception) {
            log.error("Error during check-out", e)
            Response.Status.CONFLICT to mapOf("error" to (e.message ?: "Unknown error"))
        }
    }

    fun getAttendance(employeeId: String?, date: LocalDate?): List<Attendance> {
        return if (employeeId != null && date != null) {
            attendanceDao.findByEmployeeAndDate(employeeId, date)
        } else {
            emptyList()
        }
    }
}
