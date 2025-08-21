package org.example.dao

import org.jdbi.v3.core.Jdbi
import org.example.model.Employee
import org.slf4j.LoggerFactory

class EmployeeDAO(private val jdbi: Jdbi) {

    private val log = LoggerFactory.getLogger(EmployeeDAO::class.java)

    fun insert(emp: Employee, roleId: Int, departmentId: Int): Int {
        log.info("Inserting employee: {} {}", emp.firstName, emp.lastName)
        return jdbi.withHandle<Int, Exception> { handle ->
            handle.createUpdate(
                """
                INSERT INTO employee (employee_id, first_name, last_name, role_id, department_id, reporting_to)
                VALUES (:employeeId, :firstName, :lastName, :roleId, :departmentId, :reportingTo)
                """
            )
                .bind("employeeId", emp.employeeId)
                .bind("firstName", emp.firstName)
                .bind("lastName", emp.lastName)
                .bind("roleId", roleId)
                .bind("departmentId", departmentId)
                .bind("reportingTo", emp.reportingTo)
                .execute()
        }
    }

    fun getById(id: String): Employee? {
        log.info("Fetching employee with id={}", id)
        return jdbi.withHandle<Employee?, Exception> { handle ->
            handle.createQuery(
                """
                SELECT employee_id AS "employeeId",
                       first_name AS "firstName",
                       last_name AS "lastName",
                       role_id AS "role",
                       department_id AS "department",
                       reporting_to AS "reportingTo"
                FROM employee
                WHERE employee_id = :id
                """
            )
                .bind("id", id)
                .mapTo(Employee::class.java)
                .findOne()
                .orElse(null)
        }
    }

    fun getAll(): List<Employee> {
        log.info("Fetching all employees")
        return jdbi.withHandle<List<Employee>, Exception> { handle ->
            handle.createQuery(
                """
            SELECT employee_id AS "employeeId",
                   first_name AS "firstName",
                   last_name AS "lastName",
                   role_id AS "role",
                   department_id AS "department",
                   reporting_to AS "reportingTo"
            FROM employee
            """
            )
                .mapTo(Employee::class.java)
                .list()
        }
    }

    fun delete(id: String): Int {
        log.info("Deleting employee with id={}", id)
        return jdbi.withHandle<Int, Exception> { handle ->
            handle.createUpdate("DELETE FROM employee WHERE employee_id = :id")
                .bind("id", id)
                .execute()
        }
    }
}
