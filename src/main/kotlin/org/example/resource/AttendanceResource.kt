package org.example.resource

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.example.model.Attendance
import org.example.service.EmployeeService
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.example.dto.CheckInRequest
import org.example.dto.CheckOutRequest

@Path("/attendance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AttendanceResource(private val employeeService: EmployeeService) {

    private val log = LoggerFactory.getLogger(AttendanceResource::class.java)

    @POST
    @Path("/checkin")
    fun checkIn(request: CheckInRequest): Response {
        if (request.employeeId.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "employeeId is required")).build()
        }

        val dateTime = request.dateTime?.takeIf { it.isNotBlank() }?.let {
            try {
                LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
            } catch (ex: Exception) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(mapOf("error" to "Invalid dateTime format. Use ISO_DATE_TIME."))
                    .build()
            }
        }

        return try {
            val attendance = employeeService.checkIn(request.employeeId, dateTime)
            Response.status(Response.Status.CREATED).entity(attendance).build()
        } catch (ex: NoSuchElementException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to ex.message)).build()
        } catch (ex: IllegalStateException) {
            Response.status(Response.Status.CONFLICT)
                .entity(mapOf("error" to ex.message)).build()
        }
    }

    @PUT
    @Path("/checkout")
    fun checkOut(request: CheckOutRequest): Response {
        if (request.employeeId.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "employeeId is required")).build()
        }

        val dateTime = request.dateTime?.takeIf { it.isNotBlank() }?.let {
            try {
                LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
            } catch (ex: Exception) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(mapOf("error" to "Invalid dateTime format. Use ISO_DATE_TIME."))
                    .build()
            }
        }

        return try {
            val attendance = employeeService.checkOut(request.employeeId, dateTime)
            Response.ok(attendance).build()
        } catch (ex: NoSuchElementException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to ex.message)).build()
        }
    }

    @GET
    fun getAttendance(
        @QueryParam("employeeId") employeeId: String?,
        @QueryParam("date") dateStr: String?
    ): Response {
        val date = dateStr?.takeIf { it.isNotBlank() }?.let {
            try {
                LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
            } catch (ex: Exception) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(mapOf("error" to "Invalid date format. Use yyyy-MM-dd."))
                    .build()
            }
        }

        return try {
            val attendanceList = employeeService.getAttendance(employeeId, date)
            if (attendanceList.isEmpty()) {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "No attendance records found")).build()
            } else {
                Response.ok(attendanceList).build()
            }
        } catch (ex: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to ex.message)).build()
        }
    }
}
