package com.ryzingtitan.obdtrakapi.domain.datalogs.dtos

import java.time.Instant

data class Datalog(
    val sessionId: Int,
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
