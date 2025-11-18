package com.ryzingtitan.obdtrakapi.data.datalogs.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("datalogs")
data class DatalogEntity(
    @Id
    val id: Long? = null,
    val sessionId: Int?,
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
