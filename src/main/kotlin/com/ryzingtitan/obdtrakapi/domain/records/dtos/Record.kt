package com.ryzingtitan.obdtrakapi.domain.records.dtos

import java.time.Instant
import java.util.UUID

data class Record(
    val sessionId: UUID,
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
    val oilPressure: Float?,
    val manifoldPressure: Float?,
    val massAirFlow: Float?,
)
