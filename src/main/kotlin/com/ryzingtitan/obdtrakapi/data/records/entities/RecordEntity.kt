package com.ryzingtitan.obdtrakapi.data.records.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("records")
data class RecordEntity(
    @Id
    val id: UUID? = null,
    val sessionId: UUID? = null,
    val timestamp: Instant,
    val longitude: Double,
    val latitude: Double,
    val altitude: Float,
    val intakeAirTemperature: Int?,
    val boostPressure: Float?,
    val coolantTemperature: Int?,
    val engineRpm: Int?,
    val speed: Int?,
    val throttlePosition: Float?,
    val airFuelRatio: Float?,
)
