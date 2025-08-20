package org.example.model

enum class Department (val displayName : Int){
    MARKETING(1),
    FINANCE(2),
    IT(3);
    companion object {
        fun fromId(id: Int): Department? = entries.find { it.displayName == id }
        fun fromName(name: String): Department? = entries.find { it.name.equals(name, ignoreCase = true) }
    }
}