package com.ryzingtitan.obdtrakapi.domain.sessions.dtos

import java.time.Instant
import java.util.UUID

data class Session(
    val id: UUID,
    val startTime: Instant,
    val endTime: Instant,
    val trackName: String,
    val trackLatitude: Double,
    val trackLongitude: Double,
    val carYear: Int,
    val carMake: String,
    val carModel: String,
)
