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


