package org.example.config

import io.dropwizard.core.Configuration
import io.dropwizard.db.DataSourceFactory
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

class AttendanceConfiguration : Configuration() {

    @Valid
    @NotNull
    @JsonProperty("database")
    val database: DataSourceFactory = DataSourceFactory()
}
