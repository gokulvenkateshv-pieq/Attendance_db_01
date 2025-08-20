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

    // -------------------- Check-in --------------------

    @POST
    @Path("/checkin")
    @Consumes(MediaType.APPLICATION_JSON)
    fun checkIn(request: CheckInRequest): Response {
        if (request.employeeId.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "employeeId is required"))
                .build()
        }

        val dateTime = request.dateTime?.let {
            try {
                LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
            } catch (e: Exception) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(mapOf("error" to "Invalid dateTime format. Use ISO_DATE_TIME."))
                    .build()
            }
        }

        val (status, body) = employeeService.checkIn(request.employeeId, dateTime)
        return Response.status(status).entity(body).build()
    }


    // -------------------- Check-out --------------------
    @PUT
    @Path("/checkout")
    fun checkOut(request: CheckOutRequest): Response {
        if (request.employeeId.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "employeeId is required"))
                .build()
        }

        val dateTime = request.dateTime?.let {
            try {
                LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
            } catch (e: Exception) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(mapOf("error" to "Invalid dateTime format. Use ISO_DATE_TIME."))
                    .build()
            }
        }

        val (status, body) = employeeService.checkOut(request.employeeId, dateTime)
        return Response.status(status).entity(body).build()
    }

    // -------------------- Get Attendance --------------------
    @GET
    fun getAttendance(
        @QueryParam("employeeId") employeeId: String?,
        @QueryParam("date") dateStr: String?
    ): Response {
        val date = dateStr?.let {
            try {
                LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
            } catch (e: Exception) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(mapOf("error" to "Invalid date format. Use yyyy-MM-dd."))
                    .build()
            }
        }

        val attendanceList: List<Attendance> = employeeService.getAttendance(employeeId, date)
        return Response.ok(attendanceList).build()
    }
}
