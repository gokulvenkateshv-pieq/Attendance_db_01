package org.example.model



enum class Role(val displayName: Int) {
    INTERN(1),
    DEVELOPER(2),
    MANAGER(3);
    companion object {
        fun fromId(id: Int): Role? = entries.find { it.displayName == id }
        fun fromName(name: String): Role? = entries.find { it.name.equals(name, ignoreCase = true) }
    }
}
