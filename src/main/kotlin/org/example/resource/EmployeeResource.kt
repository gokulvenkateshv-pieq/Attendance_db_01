package org.example.resource

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.example.model.Department
import org.example.model.Employee
import org.example.model.Role
import org.example.service.EmployeeService
import org.slf4j.LoggerFactory

@Path("/employee")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class EmployeeResource(private val employeeService: EmployeeService) {

    private val log = LoggerFactory.getLogger(EmployeeResource::class.java)

    @POST
    fun addEmployee(employee: Employee): Response {
        log.info("Adding employee: ${employee.firstName} ${employee.lastName}")

        val departmentEnum = Department.fromName(employee.department.trim())
            ?: return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Invalid department '${employee.department}'"))
                .build()

        val roleEnum = Role.fromName(employee.role.trim())
            ?: return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Invalid role '${employee.role}'"))
                .build()

        return try {
            val emp = employeeService.addEmployee(
                firstName = employee.firstName,
                lastName = employee.lastName,
                role = roleEnum,
                department = departmentEnum,
                reportingTo = employee.reportingTo
            )
            Response.status(Response.Status.CREATED).entity(emp).build()
        } catch (ex: Exception) {
            log.error("Error adding employee", ex)
            Response.status(Response.Status.CONFLICT)
                .entity(mapOf("error" to (ex.message ?: "Unknown error")))
                .build()
        }
    }

    @GET
    @Path("/{id}")
    fun getEmployee(@PathParam("id") id: String): Response {
        return try {
            val emp = employeeService.getEmployee(id)
            Response.ok(emp).build()
        } catch (ex: NoSuchElementException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to ex.message))
                .build()
        }
    }

    @GET
    fun getAllEmployees(): Response {
        val employees = employeeService.getAllEmployees() // no limit argument
        return Response.ok(employees).build()
    }


    @DELETE
    @Path("/{id}")
    fun deleteEmployee(@PathParam("id") id: String): Response {
        return try {
            employeeService.deleteEmployee(id)
            Response.ok(mapOf("message" to "Employee deleted successfully")).build()
        } catch (ex: NoSuchElementException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to ex.message))
                .build()
        }
    }
}
