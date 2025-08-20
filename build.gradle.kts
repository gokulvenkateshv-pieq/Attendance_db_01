plugins {
    kotlin("jvm") version "2.1.21"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("org.example.service.AttendanceApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Dropwizard core + JDBI
    implementation("io.dropwizard:dropwizard-core:4.0.15")
    implementation("io.dropwizard:dropwizard-jdbi3:4.0.15")

    // JDBI core, SQL object, Kotlin support
    implementation("org.jdbi:jdbi3-core:3.45.1")
    implementation("org.jdbi:jdbi3-sqlobject:3.45.1")
    implementation("org.jdbi:jdbi3-kotlin:3.45.1")

    // PostgreSQL driver
    implementation("org.postgresql:postgresql:42.7.2")

    // HikariCP
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Jackson Kotlin + JavaTime
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.0")

    // Kotlin stdlib
    implementation(kotlin("stdlib"))

    // Jakarta Validation
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
}

tasks.test { useJUnitPlatform() }

kotlin {
    jvmToolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

tasks.named<JavaExec>("run") {
    args("server", "src/main/resources/config.yml")
    jvmArgs = listOf("-Duser.timezone=Asia/Kolkata")
}

