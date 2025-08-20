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

        val (status, body) = employeeService.addEmployee(
            firstName = employee.firstName,
            lastName = employee.lastName,
            role = roleEnum,
            department = departmentEnum,
            reportingTo = employee.reportingTo
        )

        return Response.status(status).entity(body).build()
    }

    @GET
    @Path("/{id}")
    fun getEmployee(@PathParam("id") id: String): Response {
        val (status, body) = employeeService.getEmployee(id)
        return Response.status(status).entity(body).build()
    }

    @GET
    fun getAllEmployees(@QueryParam("limit") @DefaultValue("20") limit: Int): Response {
        val employees = employeeService.getAllEmployees(limit)
        return Response.ok(employees).build()
    }

    @DELETE
    @Path("/{id}")
    fun deleteEmployee(@PathParam("id") id: String): Response {
        val (status, body) = employeeService.deleteEmployee(id)
        return Response.status(status).entity(body).build()
    }

//    @POST
//    @Path("/login")
//    fun login(@QueryParam("employeeId") employeeId: String, @QueryParam("password") password: String): Response {
//        val (status, body) = employeeService.login(employeeId, password)
//        return Response.status(status).entity(body).build()
//    }
}



//package org.example.resource
//
//import jakarta.ws.rs.*
//import jakarta.ws.rs.core.MediaType
//import jakarta.ws.rs.core.Response
//import org.example.model.Department
//import org.example.model.Employee
//import org.example.model.Role
////import org.example.model.Role
////import org.example.model.Department
//import org.example.service.EmployeeService
//import org.slf4j.LoggerFactory
//import java.util.UUID
//
//@Path("/employee")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
//class EmployeeResource(private val employeeService: EmployeeService) {
//
//    private val log = LoggerFactory.getLogger(EmployeeResource::class.java)
//
//    @POST
//    fun addEmployee(employee: Employee): Response {
//        log.info("Adding employee: ")
//        val departmentEnum = Department.fromName(employee.department.trim())
//        if (departmentEnum == null) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                .entity(mapOf("error" to "Invalid department '${employee.department}'"))
//                .build()
//        }
//        val roleEnum = Role.fromName(employee.role.trim())
//        if (roleEnum == null) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                .entity(mapOf("error" to "Invalid role '${employee.role}'"))
//                .build()
//        }
//        val (status, body) = employeeService.addEmployee(
//            firstName = employee.firstName,
//            lastName = employee.lastName,
//            role = roleEnum,
//            department = departmentEnum,
//            reportingTo = employee.reportingTo// optional
//        )
//
//        return Response.status(status).entity(body).build()
//    }
//
//    @GET
//    @Path("/{id}")
//    fun getEmployee(@PathParam("id") id: String): Response {
//        val (status, body) = employeeService.getEmployee(id)
//        return Response.status(status).entity(body).build()
//    }
//
//    @GET
//    fun getAllEmployees(@QueryParam("limit") @DefaultValue("20") limit: Int): Response {
//        val employees = employeeService.getAllEmployees(limit)
//        return Response.ok(employees).build()
//    }
//
//    @DELETE
//    @Path("/{id}")
//    fun deleteEmployee(@PathParam("id") id: String): Response {
//        val uuid = try {
//            UUID.fromString(id)
//        } catch (e: IllegalArgumentException) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                .entity(mapOf("error" to "Invalid UUID format"))
//                .build()
//        }
//        val (status, body) = employeeService.deleteEmployee(uuid)
//        return Response.status(status).entity(body).build()
//    }
//
//    @POST
//    @Path("/login")
//    fun login(@QueryParam("employeeId") employeeId: String, @QueryParam("password") password: String): Response {
//
//        val (status, body) = employeeService.login(uuid, password)
//        return Response.status(status).entity(body).build()
//    }
//}
