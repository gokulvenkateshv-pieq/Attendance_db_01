package org.example.service

import org.example.config.AttendanceConfiguration
import org.example.dao.EmployeeDAO
import org.example.dao.AttendanceDAO
import org.example.resource.EmployeeResource
import org.example.resource.AttendanceResource

import io.dropwizard.core.Application
import io.dropwizard.core.setup.Bootstrap
import io.dropwizard.core.setup.Environment
import io.dropwizard.jdbi3.JdbiFactory

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.core.kotlin.KotlinPlugin
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.databind.SerializationFeature

import org.eclipse.jetty.servlets.CrossOriginFilter
import java.util.EnumSet

class AttendanceApplication : Application<AttendanceConfiguration>() {

    override fun initialize(bootstrap: Bootstrap<AttendanceConfiguration>) {
        // Configure Jackson modules
        bootstrap.objectMapper.registerModule(kotlinModule())
        bootstrap.objectMapper.registerModule(JavaTimeModule())
        bootstrap.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    override fun run(configuration: AttendanceConfiguration, environment: Environment) {
        // Setup JDBI
        val jdbi: Jdbi = JdbiFactory().build(environment, configuration.database, "postgresql")
        jdbi.installPlugin(SqlObjectPlugin())
        jdbi.installPlugin(KotlinPlugin())

        // Initialize DAOs
        val employeeDao = EmployeeDAO(jdbi)
        val attendanceDao = AttendanceDAO(jdbi)

        // Initialize service
        val employeeService = EmployeeService(employeeDao, attendanceDao)

        // Register resources
        environment.jersey().register(EmployeeResource(employeeService))
        environment.jersey().register(AttendanceResource(employeeService))

        // Enable CORS
        val cors = environment.servlets().addFilter("CORS", CrossOriginFilter::class.java)
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "http://localhost:3000")
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization")
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD")
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true")
        cors.addMappingForUrlPatterns(EnumSet.allOf(jakarta.servlet.DispatcherType::class.java), true, "/*")
    }
}

fun main(args: Array<String>) {
    AttendanceApplication().run(*args)
}
