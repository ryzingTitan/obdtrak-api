package com.ryzingtitan.obdtrakapi.domain.sessions.dtos

import java.time.Instant

data class Session(
    val id: Int,
    val startTime: Instant,
    val endTime: Instant,
    val trackName: String,
    val trackLatitude: Double,
    val trackLongitude: Double,
    val carYear: Int,
    val carMake: String,
    val carModel: String,
)
