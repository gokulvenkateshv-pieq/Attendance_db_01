package org.example.service

// Importing configuration and DAOs
import org.example.config.AttendanceConfiguration
import org.example.dao.EmployeeDAO
import org.example.dao.AttendanceDAO

// Importing REST resources
import org.example.resource.EmployeeResource
import org.example.resource.AttendanceResource

// Dropwizard core classes
import io.dropwizard.core.Application
import io.dropwizard.core.setup.Bootstrap
import io.dropwizard.core.setup.Environment
import io.dropwizard.jdbi3.JdbiFactory

// JDBI database library
import org.jdbi.v3.core.Jdbi

// Jackson modules for Kotlin and Java Time
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.databind.SerializationFeature

class AttendanceApplication : Application<AttendanceConfiguration>() {

    override fun initialize(bootstrap: Bootstrap<AttendanceConfiguration>) {
        bootstrap.objectMapper.registerModule(kotlinModule())
        bootstrap.objectMapper.registerModule(JavaTimeModule())
        bootstrap.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    override fun run(configuration: AttendanceConfiguration, environment: Environment) {
        // Setup JDBI database connection using the configuration
        val jdbi: Jdbi = JdbiFactory().build(environment, configuration.database, "postgresql")


        jdbi.installPlugin(org.jdbi.v3.core.kotlin.KotlinPlugin())

        // Initialize DAOs for Employee and Attendance
        val employeeDao = EmployeeDAO(jdbi)
        val attendanceDao = AttendanceDAO(jdbi)

        // Initialize service layer with DAOs
        val employeeService = EmployeeService(employeeDao, attendanceDao)

        // Register REST resources with Jersey (HTTP endpoints)
        environment.jersey().register(EmployeeResource(employeeService))
        environment.jersey().register(AttendanceResource(employeeService))

    }
}

fun main(args: Array<String>) {
    AttendanceApplication().run(*args)
}
