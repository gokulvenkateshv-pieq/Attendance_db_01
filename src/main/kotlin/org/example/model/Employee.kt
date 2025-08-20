package org.example.model


data class Employee(
    val firstName: String,
    val lastName: String,
    val role: String,
    val department: String,
    val reportingTo: String? = null // Nullable string, storing manager ID or name
) {
    var employeeId: String = ""

    companion object {
        private var idCounter = 100
    }

    fun generateId(): String {
        if (employeeId.isBlank()) {
            employeeId = "${firstName.first().uppercase()}${lastName.first().uppercase()}$idCounter"
            idCounter++
        }
        return employeeId
    }

    fun validate(): Boolean {
        val nameRegex = Regex("^[A-Za-z]+$")
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                nameRegex.matches(firstName) &&
                nameRegex.matches(lastName)
    }
}


//package org.example.model
//
//data class Employee(
//    val firstName: String,
//    val lastName: String,
//    val role: Role,
//    val department: Department,
//    val reportingTo: String? = null
//) {
//    var employeeId: String = ""
//
//    val roleName: Int
//        get() = role.displayName  // maps enum to string for DB
//
//    val departmentName: Int
//        get() = department.displayName  // maps enum to string for DB
//
//    companion object {
//        private var idCounter = 100
//    }
//
//    fun validate(): Boolean {
//        val nameRegex = Regex("^[A-Za-z]+$")
//        val isValid = firstName.isNotBlank() &&
//                lastName.isNotBlank() &&
//                nameRegex.matches(firstName) &&
//                nameRegex.matches(lastName)
//
//        if (isValid) {
//            employeeId = "${firstName.first().uppercase()}${lastName.first().uppercase()}$idCounter"
//            idCounter++
//        }
//
//        return isValid
//    }
//}
