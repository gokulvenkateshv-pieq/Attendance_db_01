###Employee_Attendance_Management_System

##Overview

This application manages employee attendance using a RESTful API built with Dropwizard and PostgreSQL. It supports employee registration, check-in, check-out, and attendance summaries.


### How it works

- When the app starts (AttendanceApplication) the main function gets executed. It first reads the config.yml file, which contains the server ports and database configuration. This configuration is then mapped to the AttendanceConfiguration class.

- Next, the application uses JDBI along with this configuration to connect to PostgreSQL and establish the database connection.

### Then the application creates DAO instances:

- **EmployeeDAO** -> deals with all employee-related database operations

- **AttendanceDAO** -> deals with  all attendance-related database operations

After the DAOs are created,
### the **EmployeeService** is created:

- The Service Layer contains all the business logic (validation) of the application

### Then the application registers the Resources:

- **AttendanceResource** -> Contains all Endpoints related to attendance

- **EmployeeResource** -> Contains all Endpoints related to employees

- The Resource Layer is the entry point for client requests. It forwards requests to the service layer and returns responses back to the client.

**flow** => Client -> Resource -> Service -> DAO ->Database -> DAO -> Service -> Resource -> Client


```

# Folder structure

attendance_project/
build.gradle.kts - Gradle build file with dependencies and tasks
Resources/
config.yml  - Application configuration: server ports & DB settings

config/
      AttendanceConfiguration.kt - Maps config.yml and holds DB config values that the application uses to connect to PostgreSQL.
dao/
   EmployeeDAO.kt - Handles all employee-related DB operations (insert, fetch, delete)
   AttendanceDAO.kt - Handles all attendance-related DB operations (check-in/out, fetch records)
dto/
   CheckInRequest.kt -  DTO for check-in API request
   CheckOutRequest.kt - DTO for check-out API request
   SummaryRequest.kt  - DTO for summary API request
   SummaryDTO.kt  -  DTO for summary API response
model/
  Employee.kt - Employee model with fields and ID generation logic
  Attendance.kt - Attendance model; holds check-in/out times & worked hours
  Role.kt -  Enum for employee roles (INTERN, DEVELOPER, MANAGER)
  Department.kt-  Enum for departments (MARKETING, FINANCE, IT)
  Manager.kt -  Enum for predefined managers

 resource/
 EmployeeResource.kt -  endpoints for employee operations
AttendanceResource.kt - endpoints for attendance operations

service/
EmployeeService.kt - Business logic; connects DAO and REST layer
AttendanceApplication.kt - Main Dropwizard application; sets up JDBI, DAOs, Service, Resources      

```
