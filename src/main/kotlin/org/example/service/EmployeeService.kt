package org.example.service

import org.example.model.Employee
import org.example.dao.EmployeeDAO
import org.example.model.Role
import org.example.model.Department
import org.example.model.Attendance
import org.example.dao.AttendanceDAO
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime

class EmployeeService(
    private val employeeDao: EmployeeDAO,
    private val attendanceDao: AttendanceDAO
) {
    private val log = LoggerFactory.getLogger(EmployeeService::class.java)

    // -------------------- Employee CRUD --------------------

    fun addEmployee(firstName: String, lastName: String, role: Role, department: Department, reportingTo: String?): Employee {
        val emp = Employee(
            firstName = firstName,
            lastName = lastName,
            role = role.name,
            department = department.name,
            reportingTo = reportingTo
        )
        emp.generateId()
        employeeDao.insert(emp, role.ordinal + 1, department.ordinal + 1)
        log.info("Employee added successfully id=${emp.employeeId}")
        return emp
    }

    fun getEmployee(id: String): Employee {
        return employeeDao.getById(id) ?: throw NoSuchElementException("Employee not found")
    }

    fun getAllEmployees(): List<Employee> = employeeDao.getAll()

    fun deleteEmployee(id: String) {
        val deleted = employeeDao.delete(id)
        if (deleted == 0) throw NoSuchElementException("Employee not found")
    }

    // -------------------- Attendance --------------------

    fun checkIn(employeeId: String, dateTime: LocalDateTime? = null): Attendance {
        val emp = employeeDao.getById(employeeId) ?: throw NoSuchElementException("Employee ID $employeeId does not exist")
        val now = (dateTime ?: LocalDateTime.now()).withSecond(0).withNano(0)

        val sameTimeAttendance = attendanceDao.findByEmployeeAndDate(employeeId, now.toLocalDate())
            .any { it.dateTimeOfCheckIn.withSecond(0).withNano(0) == now }

        if (sameTimeAttendance) throw IllegalStateException("Cannot check in multiple times at the same minute")

        val attendance = Attendance(employeeId, now)
        attendanceDao.insert(attendance)
        return attendance
    }

    fun checkOut(employeeId: String, dateTime: LocalDateTime? = null): Attendance {
        val today = LocalDate.now()
        val openAttendance = attendanceDao.findOpenAttendanceForToday(employeeId, today)
            ?: throw NoSuchElementException("No active check-in found for today")

        val now = (dateTime ?: LocalDateTime.now()).withSecond(0).withNano(0)
        openAttendance.checkOut(now)
        attendanceDao.update(openAttendance)
        return openAttendance
    }

    fun getAttendance(employeeId: String?, date: LocalDate?): List<Attendance> {
        if (employeeId.isNullOrBlank()) {
            throw IllegalArgumentException("employeeId is required")
        }
        if (date == null) {
            throw IllegalArgumentException("date is required")
        }

        val list = attendanceDao.findByEmployeeAndDate(employeeId, date)
        if (list.isEmpty()) {
            throw NoSuchElementException("No attendance records found")
        }
        return list
    }

}
