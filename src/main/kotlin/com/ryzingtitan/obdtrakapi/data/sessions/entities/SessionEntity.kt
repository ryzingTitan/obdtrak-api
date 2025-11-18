package com.ryzingtitan.obdtrakapi.data.sessions.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("sessions")
data class SessionEntity(
    @Id
    val id: Int? = null,
    val userEmail: String,
    val userFirstName: String,
    val userLastName: String,
    val startTime: Instant,
    val endTime: Instant,
    val trackId: UUID,
    val carId: UUID,
)
