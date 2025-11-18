package com.ryzingtitan.obdtrakapi.domain.tracks.dtos

import java.util.UUID

data class Track(
    val id: UUID,
    val name: String,
    val latitude: Double,
    val longitude: Double,
)
